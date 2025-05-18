package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.comun.entity.Notificacion;
import org.grupo_h.comun.exceptions.ResourceNotFoundException;
import org.grupo_h.comun.repository.EmpleadoRepository;
import org.grupo_h.comun.repository.NotificacionRepository;
import org.grupo_h.empleados.dto.notificacion.NotificacionDTO;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

@Service
public class NotificacionServiceImpl implements NotificacionService {

    private static final Logger logger = LoggerFactory.getLogger(NotificacionServiceImpl.class);
    private static final DateTimeFormatter FRIENDLY_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy 'a las' HH:mm", Locale.of("es", "ES"));


    private final NotificacionRepository notificacionRepository;
    private final EmpleadoRepository empleadoRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public NotificacionServiceImpl(NotificacionRepository notificacionRepository,
                                   EmpleadoRepository empleadoRepository,
                                   ModelMapper modelMapper) {
        this.notificacionRepository = notificacionRepository;
        this.empleadoRepository = empleadoRepository;
        this.modelMapper = modelMapper;
    }

    private Empleado getEmpleadoPorEmail(String email) {
        return empleadoRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado", "email del usuario", email));
    }

    @Override
    @Transactional
    public void crearNotificacion(Empleado empleadoDestino, String tipoEvento, String mensaje, UUID idReferenciaEntidad, String urlReferencia) {
        if (empleadoDestino == null) {
            logger.error("Intento de crear notificación para un empleadoDestino nulo. Evento: {}, Mensaje: {}", tipoEvento, mensaje);
            throw new IllegalArgumentException("El empleado destino no puede ser nulo para crear una notificación.");
        }
        Notificacion notificacion = new Notificacion();
        notificacion.setEmpleadoDestino(empleadoDestino);
        notificacion.setTipoEvento(tipoEvento);
        notificacion.setMensaje(mensaje);
        notificacion.setIdReferenciaEntidad(idReferenciaEntidad);
        notificacion.setUrlReferencia(urlReferencia);
        // fechaHoraCreacion y leida (false) se establecen por defecto en la entidad o @PrePersist

        notificacionRepository.save(notificacion);
        logger.info("Notificación creada para empleado ID: {}, Tipo: {}, Mensaje: {}", empleadoDestino.getId(), tipoEvento, mensaje);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificacionDTO> getNotificacionesParaEmpleado(String emailEmpleado, Pageable pageable) {
        Empleado empleado = getEmpleadoPorEmail(emailEmpleado);
        Page<Notificacion> notificacionesPage = notificacionRepository.findByEmpleadoDestinoOrderByFechaHoraCreacionDesc(empleado, pageable);
        return notificacionesPage.map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarNotificacionesNoLeidas(String emailEmpleado) {
        Empleado empleado = getEmpleadoPorEmail(emailEmpleado);
        return notificacionRepository.countByEmpleadoDestinoAndLeidaFalse(empleado);
    }

    @Override
    @Transactional
    public void marcarNotificacionComoLeida(UUID idNotificacion, String emailEmpleado) {
        Empleado empleado = getEmpleadoPorEmail(emailEmpleado);
        Notificacion notificacion = notificacionRepository.findById(idNotificacion)
                .orElseThrow(() -> new ResourceNotFoundException("Notificacion", "ID", idNotificacion.toString()));

        if (!notificacion.getEmpleadoDestino().getId().equals(empleado.getId())) {
            logger.warn("Intento no autorizado de marcar notificación {} como leída por empleado {}", idNotificacion, emailEmpleado);
            throw new AccessDeniedException("No tiene permiso para marcar esta notificación como leída.");
        }

        if (!notificacion.isLeida()) {
            notificacion.setLeida(true);
            notificacionRepository.save(notificacion);
            logger.info("Notificación ID {} marcada como leída para empleado {}", idNotificacion, emailEmpleado);
        } else {
            logger.debug("Notificación ID {} ya estaba marcada como leída para empleado {}", idNotificacion, emailEmpleado);
        }
    }

    @Override
    @Transactional
    public void marcarTodasLasNotificacionesComoLeidas(String emailEmpleado) {
        Empleado empleado = getEmpleadoPorEmail(emailEmpleado);
        int actualizadas = notificacionRepository.marcarTodasComoLeidasParaEmpleado(empleado);
        logger.info("{} notificaciones marcadas como leídas para el empleado {}", actualizadas, emailEmpleado);
    }

    private NotificacionDTO convertToDto(Notificacion notificacion) {
        NotificacionDTO dto = modelMapper.map(notificacion, NotificacionDTO.class);
        dto.setFechaHoraFormateada(notificacion.getFechaHoraCreacion().format(FRIENDLY_DATE_FORMATTER));
        dto.setTiempoTranscurrido(formatTiempoTranscurrido(notificacion.getFechaHoraCreacion()));
        return dto;
    }

    private String formatTiempoTranscurrido(LocalDateTime fecha) {
        Duration duracion = Duration.between(fecha, LocalDateTime.now());
        long segundos = duracion.getSeconds();

        if (segundos < 60) {
            return "Hace un momento";
        }
        long minutos = segundos / 60;
        if (minutos < 60) {
            return "Hace " + minutos + (minutos == 1 ? " minuto" : " minutos");
        }
        long horas = minutos / 60;
        if (horas < 24) {
            return "Hace " + horas + (horas == 1 ? " hora" : " horas");
        }
        long dias = horas / 24;
        if (dias < 7) {
            return "Hace " + dias + (dias == 1 ? " día" : " días");
        }
        // Para más de una semana, podrías mostrar la fecha directamente o "Hace X semanas"
        return "El " + fecha.format(DateTimeFormatter.ofLocalizedDate(java.time.format.FormatStyle.MEDIUM).withLocale(Locale.of("es", "ES")));
    }
}
