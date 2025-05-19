package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.comun.entity.chat.Colaboracion;
import org.grupo_h.comun.entity.chat.Mensaje;
import org.grupo_h.comun.repository.ColaboracionRepository;
import org.grupo_h.comun.repository.EmpleadoRepository;
import org.grupo_h.comun.repository.MensajeRepository;
import org.grupo_h.empleados.dto.mensajeria.MensajeRequestDTO;
import org.grupo_h.empleados.dto.mensajeria.MensajeResponseDTO;
import org.grupo_h.empleados.service.MensajeriaService; // Asegúrate que es la interfaz correcta
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class MensajeriaServiceImpl implements MensajeriaService {

    private static final Logger logger = LoggerFactory.getLogger(MensajeriaServiceImpl.class);

    private final MensajeRepository mensajeRepository;
    private final ColaboracionRepository colaboracionRepository;
    private final EmpleadoRepository empleadoRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public MensajeriaServiceImpl(MensajeRepository mensajeRepository,
                                 ColaboracionRepository colaboracionRepository,
                                 EmpleadoRepository empleadoRepository,
                                 ModelMapper modelMapper) {
        this.mensajeRepository = mensajeRepository;
        this.colaboracionRepository = colaboracionRepository;
        this.empleadoRepository = empleadoRepository;
        this.modelMapper = modelMapper;
    }

    private Empleado getEmpleadoPorEmail(String email) {
        return empleadoRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con email: " + email));
    }

    @Override
    @Transactional
    public MensajeResponseDTO enviarMensaje(MensajeRequestDTO dto, String emailEmisor) {
        Empleado emisor = getEmpleadoPorEmail(emailEmisor);
        Colaboracion colaboracion = colaboracionRepository.findById(dto.getIdColaboracion())
                .orElseThrow(() -> new EntityNotFoundException("Colaboración no encontrada con ID: " + dto.getIdColaboracion()));

        if (colaboracion.getEstado() != Colaboracion.EstadoColaboracion.ACTIVA) {
            throw new IllegalStateException("No se pueden enviar mensajes en una colaboración inactiva.");
        }

        // Verificar que el emisor sea parte de la colaboración
        if (!colaboracion.getEmpleadoA().equals(emisor) && !colaboracion.getEmpleadoB().equals(emisor)) {
            throw new AccessDeniedException("El emisor no forma parte de esta colaboración.");
        }

        // Determinar el receptor
        Empleado receptor = colaboracion.getEmpleadoA().equals(emisor) ? colaboracion.getEmpleadoB() : colaboracion.getEmpleadoA();

        Mensaje mensaje = new Mensaje();
        mensaje.setColaboracion(colaboracion);
        mensaje.setEmisor(emisor);
        mensaje.setReceptor(receptor);
        mensaje.setMensaje(dto.getContenido());
        mensaje.setFechaEmision(LocalDateTime.now());

        if (dto.getIdMensajeRespuestaA() != null) {
            Mensaje mensajeRespondido = mensajeRepository.findById(dto.getIdMensajeRespuestaA())
                    .orElseThrow(() -> new EntityNotFoundException("Mensaje a responder no encontrado con ID: " + dto.getIdMensajeRespuestaA()));
            // Validar que el mensaje respondido pertenezca a la misma colaboración
            if (!mensajeRespondido.getColaboracion().equals(colaboracion)) {
                throw new IllegalArgumentException("No se puede responder a un mensaje de otra colaboración.");
            }
            mensaje.setMensajeRespuesta(mensajeRespondido);
        }

        Mensaje guardado = mensajeRepository.save(mensaje);
        logger.info("Mensaje ID: {} enviado en colaboración ID: {} de {} para {}", guardado.getId(), colaboracion.getId(), emisor.getUsuario().getEmail(), receptor.getUsuario().getEmail());

        // Aquí podrías publicar un evento si necesitas notificaciones en tiempo real para el receptor del mensaje
        // eventPublisher.publishEvent(new NuevoMensajeEvent(this, guardado));

        return convertToMensajeResponseDTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MensajeResponseDTO> listarMensajesColaboracion(UUID idColaboracion, String emailUsuarioActual, Pageable pageable) {
        Empleado usuarioActual = getEmpleadoPorEmail(emailUsuarioActual);
        Colaboracion colaboracion = colaboracionRepository.findById(idColaboracion)
                .orElseThrow(() -> new EntityNotFoundException("Colaboración no encontrada con ID: " + idColaboracion));

        // Verificar que el usuario actual sea parte de la colaboración
        if (!colaboracion.getEmpleadoA().equals(usuarioActual) && !colaboracion.getEmpleadoB().equals(usuarioActual)) {
            throw new AccessDeniedException("No tiene permiso para ver los mensajes de esta colaboración.");
        }

        return mensajeRepository.findByColaboracionOrderByFechaEmisionAsc(colaboracion, pageable)
                .map(this::convertToMensajeResponseDTO);
    }

    private MensajeResponseDTO convertToMensajeResponseDTO(Mensaje mensaje) {
        MensajeResponseDTO dto = modelMapper.map(mensaje, MensajeResponseDTO.class);
        if (mensaje.getEmisor() != null && mensaje.getEmisor().getUsuario() != null) {
            dto.setIdEmisor(mensaje.getEmisor().getId());
            dto.setNombreEmisor(mensaje.getEmisor().getNombreCompleto());
            dto.setEmailEmisor(mensaje.getEmisor().getUsuario().getEmail());
        }
        if (mensaje.getReceptor() != null && mensaje.getReceptor().getUsuario() != null) {
            dto.setIdReceptor(mensaje.getReceptor().getId());
            dto.setNombreReceptor(mensaje.getReceptor().getNombreCompleto());
            dto.setEmailReceptor(mensaje.getReceptor().getUsuario().getEmail());
        }
        if (mensaje.getMensajeRespuesta() != null) {
            dto.setIdMensajeRespuestaA(mensaje.getMensajeRespuesta().getId());
        }
        dto.setIdColaboracion(mensaje.getColaboracion().getId());
        return dto;
    }
}