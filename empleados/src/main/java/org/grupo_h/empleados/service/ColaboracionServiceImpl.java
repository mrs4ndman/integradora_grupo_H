package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.comun.entity.Usuario;
import org.grupo_h.comun.entity.chat.Colaboracion;
import org.grupo_h.comun.entity.chat.Colaboracion.EstadoColaboracion;
import org.grupo_h.comun.entity.chat.PeriodoColaboracion;
import org.grupo_h.comun.entity.chat.SolicitudColaboracion;
import org.grupo_h.comun.entity.chat.SolicitudColaboracion.EstadoSolicitud;
import org.grupo_h.comun.exceptions.ResourceNotFoundException;
import org.grupo_h.comun.repository.ColaboracionRepository;
import org.grupo_h.comun.repository.EmpleadoRepository;
import org.grupo_h.comun.repository.PeriodoColaboracionRepository;
import org.grupo_h.comun.repository.SolicitudColaboracionRepository;
import org.grupo_h.empleados.dto.colaboracion.*;
import org.grupo_h.empleados.dto.EmpleadoSimpleDTO; // Importar DTO para búsqueda
import org.grupo_h.empleados.event.ColaboracionCanceladaEvent;
import org.grupo_h.empleados.event.ColaboracionIniciadaEvent;
import org.grupo_h.empleados.event.SolicitudAceptadaEvent;
import org.grupo_h.empleados.event.SolicitudColaboracionRecibidaEvent;
import org.grupo_h.empleados.event.SolicitudRechazadaEvent;
import org.grupo_h.empleados.service.ColaboracionService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementación del servicio para la gestión de colaboraciones.
 */
@Service
public class ColaboracionServiceImpl implements ColaboracionService {

    private static final Logger logger = LoggerFactory.getLogger(ColaboracionServiceImpl.class);

    private final EmpleadoRepository empleadoRepository;
    private final SolicitudColaboracionRepository solicitudColaboracionRepository;
    private final ColaboracionRepository colaboracionRepository;
    private final PeriodoColaboracionRepository periodoColaboracionRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public ColaboracionServiceImpl(EmpleadoRepository empleadoRepository,
                                   SolicitudColaboracionRepository solicitudColaboracionRepository,
                                   ColaboracionRepository colaboracionRepository,
                                   PeriodoColaboracionRepository periodoColaboracionRepository,
                                   ModelMapper modelMapper,
                                   ApplicationEventPublisher eventPublisher) {
        this.empleadoRepository = empleadoRepository;
        this.solicitudColaboracionRepository = solicitudColaboracionRepository;
        this.colaboracionRepository = colaboracionRepository;
        this.periodoColaboracionRepository = periodoColaboracionRepository;
        this.modelMapper = modelMapper;
        this.eventPublisher = eventPublisher;
    }

    private Empleado getEmpleadoPorEmailUsuario(String emailUsuario) {
        return empleadoRepository.findByUsuarioEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado", "email de usuario", emailUsuario));
    }

    private Empleado getEmpleadoPorId(UUID id) { // Aunque no se use en crearSolicitud ahora, puede ser útil en otros lados
        return empleadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado", "ID", id.toString()));
    }


    @Override
    @Transactional
    public SolicitudColaboracionResponseDTO crearSolicitud(SolicitudColaboracionRequestDTO requestDTO, String emailEmpleadoEmisor) {
        if (emailEmpleadoEmisor == null || emailEmpleadoEmisor.isEmpty()) {
            throw new IllegalArgumentException("El email del empleado emisor no puede ser nulo o vacío.");
        }
        if (requestDTO.getEmailEmpleadoDestino() == null || requestDTO.getEmailEmpleadoDestino().isEmpty()) {
            throw new IllegalArgumentException("El email del empleado destino no puede ser nulo o vacío.");
        }

        Empleado emisor = getEmpleadoPorEmailUsuario(emailEmpleadoEmisor);
        Empleado receptor = getEmpleadoPorEmailUsuario(requestDTO.getEmailEmpleadoDestino()); // Buscamos por email

        if (emisor.getId().equals(receptor.getId())) {
            throw new IllegalArgumentException("Un empleado no puede enviarse una solicitud de colaboración a sí mismo.");
        }

        Optional<Colaboracion> colaboracionActivaOpt = colaboracionRepository
                .findColaboracionActivaEntreEmpleados(emisor, receptor, EstadoColaboracion.ACTIVA);
        if (colaboracionActivaOpt.isPresent()){
            logger.warn("Intento de crear solicitud entre {} y {} fallido: Ya existe colaboración activa ID {}",
                    (emisor.getUsuario() != null ? emisor.getUsuario().getEmail() : "ID:" + emisor.getId()),
                    (receptor.getUsuario() != null ? receptor.getUsuario().getEmail() : "ID:" + receptor.getId()),
                    colaboracionActivaOpt.get().getId());
            throw new IllegalStateException("Ya existe una colaboración activa entre estos empleados.");
        }

        Optional<SolicitudColaboracion> solicitudPendienteOpt = solicitudColaboracionRepository
                .findByEmpleadoEmisorAndEmpleadoReceptorAndEstado(emisor, receptor, EstadoSolicitud.PENDIENTE);
        if (solicitudPendienteOpt.isPresent()) {
            logger.warn("Intento de crear solicitud de {} para {} fallido: Ya existe solicitud pendiente ID {}",
                    (emisor.getUsuario() != null ? emisor.getUsuario().getEmail() : "ID:" + emisor.getId()),
                    (receptor.getUsuario() != null ? receptor.getUsuario().getEmail() : "ID:" + receptor.getId()),
                    solicitudPendienteOpt.get().getId());
            throw new IllegalStateException("Ya existe una solicitud pendiente de este emisor para este receptor.");
        }
        solicitudPendienteOpt = solicitudColaboracionRepository
                .findByEmpleadoEmisorAndEmpleadoReceptorAndEstado(receptor, emisor, EstadoSolicitud.PENDIENTE);
        if (solicitudPendienteOpt.isPresent()) {
            logger.warn("Intento de crear solicitud de {} para {} fallido: El receptor ya tiene una solicitud pendiente para el emisor (ID {})",
                    (emisor.getUsuario() != null ? emisor.getUsuario().getEmail() : "ID:" + emisor.getId()),
                    (receptor.getUsuario() != null ? receptor.getUsuario().getEmail() : "ID:" + receptor.getId()),
                    solicitudPendienteOpt.get().getId());
            throw new IllegalStateException("Ya existe una solicitud pendiente de este receptor para este emisor. Por favor, responda a esa solicitud.");
        }

        SolicitudColaboracion solicitud = new SolicitudColaboracion();
        solicitud.setEmpleadoEmisor(emisor);
        solicitud.setEmpleadoReceptor(receptor);
        solicitud.setMensaje(requestDTO.getMensaje());
        solicitud.setFechaSolicitud(LocalDateTime.now());
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);

        SolicitudColaboracion guardada = solicitudColaboracionRepository.save(solicitud);
        logger.info("Solicitud de colaboración ID: {} creada por '{}' para '{}'",
                guardada.getId(),
                (emisor.getUsuario() != null ? emisor.getUsuario().getEmail() : "ID:" + emisor.getId()),
                (receptor.getUsuario() != null ? receptor.getUsuario().getEmail() : "ID:" + receptor.getId()));

        eventPublisher.publishEvent(new SolicitudColaboracionRecibidaEvent(this, guardada));

        return convertToSolicitudResponseDTO(guardada, requestDTO.getFechaInicioPeriodoPropuesto(), requestDTO.getFechaFinPeriodoPropuesto());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SolicitudColaboracionResponseDTO> listarSolicitudesEmitidas(String emailEmpleadoEmisor, Pageable pageable) {
        Empleado emisor = getEmpleadoPorEmailUsuario(emailEmpleadoEmisor);
        return solicitudColaboracionRepository.findByEmpleadoEmisorOrderByFechaSolicitudDesc(emisor, pageable)
                .map(sol -> convertToSolicitudResponseDTO(sol, null, null));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SolicitudColaboracionResponseDTO> listarSolicitudesRecibidas(String emailEmpleadoReceptor, Pageable pageable) {
        Empleado receptor = getEmpleadoPorEmailUsuario(emailEmpleadoReceptor);
        return solicitudColaboracionRepository.findByEmpleadoReceptorOrderByFechaSolicitudDesc(receptor, pageable)
                .map(sol -> convertToSolicitudResponseDTO(sol, null, null));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SolicitudColaboracionResponseDTO> listarSolicitudesPendientesRecibidas(String emailEmpleadoReceptor, Pageable pageable) {
        Empleado receptor = getEmpleadoPorEmailUsuario(emailEmpleadoReceptor);
        return solicitudColaboracionRepository.findByEmpleadoReceptorAndEstadoOrderByFechaSolicitudDesc(receptor, EstadoSolicitud.PENDIENTE, pageable)
                .map(sol -> convertToSolicitudResponseDTO(sol, null, null));
    }

    @Override
    @Transactional(readOnly = true)
    public long contarSolicitudesPendientesRecibidas(String emailEmpleadoReceptor) {
        Empleado receptor = getEmpleadoPorEmailUsuario(emailEmpleadoReceptor);
        return solicitudColaboracionRepository.countByEmpleadoReceptorAndEstado(receptor, EstadoSolicitud.PENDIENTE);
    }

    @Override
    @Transactional
    public SolicitudColaboracionResponseDTO gestionarSolicitud(UUID idSolicitud, DecisionSolicitudRequestDTO decisionDTO, String emailEmpleadoReceptor) {
        Empleado receptor = getEmpleadoPorEmailUsuario(emailEmpleadoReceptor);
        SolicitudColaboracion solicitud = solicitudColaboracionRepository.findById(idSolicitud)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud", "ID", idSolicitud.toString()));

        if (!solicitud.getEmpleadoReceptor().getId().equals(receptor.getId())) {
            String emailReceptorSolicitud = solicitud.getEmpleadoReceptor().getUsuario() != null ? solicitud.getEmpleadoReceptor().getUsuario().getEmail() : "ID: " + solicitud.getEmpleadoReceptor().getId();
            logger.warn("Intento no autorizado por '{}' para gestionar solicitud ID {} que pertenece a '{}'", emailEmpleadoReceptor, idSolicitud, emailReceptorSolicitud);
            throw new AccessDeniedException("No tiene permiso para gestionar esta solicitud. Debe ser el receptor original.");
        }
        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            logger.warn("Intento de gestionar solicitud ID {} que no está PENDIENTE. Estado actual: {}", idSolicitud, solicitud.getEstado());
            throw new IllegalStateException("La solicitud con ID " + idSolicitud + " ya ha sido gestionada. Estado actual: " + solicitud.getEstado());
        }

        Colaboracion colaboracionCreada = null;

        if ("ACEPTAR".equalsIgnoreCase(decisionDTO.getAccion())) {
            solicitud.setEstado(EstadoSolicitud.ACEPTADA);

            Colaboracion colaboracion = new Colaboracion();
            colaboracion.setEmpleadoA(solicitud.getEmpleadoEmisor());
            colaboracion.setEmpleadoB(solicitud.getEmpleadoReceptor());
            colaboracion.setEstado(EstadoColaboracion.ACTIVA);
            colaboracion.setSolicitudes(new java.util.ArrayList<>());
            colaboracion.setPeriodos(new java.util.ArrayList<>());

            colaboracionCreada = colaboracionRepository.save(colaboracion);
            solicitud.setColaboracion(colaboracionCreada);

            PeriodoColaboracion periodo = new PeriodoColaboracion();
            periodo.setColaboracion(colaboracionCreada);
            periodo.setFechaInicio(LocalDateTime.now());
            periodo.setEmpleadoInicio(receptor);
            periodoColaboracionRepository.save(periodo);

            logger.info("Solicitud ID: {} aceptada por '{}'. Colaboración ID: {} creada entre '{}' y '{}'.",
                    idSolicitud, emailEmpleadoReceptor, colaboracionCreada.getId(),
                    (solicitud.getEmpleadoEmisor().getUsuario() != null ? solicitud.getEmpleadoEmisor().getUsuario().getEmail() : "N/A"),
                    (receptor.getUsuario() != null ? receptor.getUsuario().getEmail() : "N/A"));
            eventPublisher.publishEvent(new SolicitudAceptadaEvent(this, solicitud));
            eventPublisher.publishEvent(new ColaboracionIniciadaEvent(this, colaboracionCreada, receptor));

        } else if ("RECHAZAR".equalsIgnoreCase(decisionDTO.getAccion())) {
            solicitud.setEstado(EstadoSolicitud.RECHAZADA);
            logger.info("Solicitud ID: {} rechazada por '{}'. Motivo: {}", idSolicitud, emailEmpleadoReceptor, decisionDTO.getMensajeRespuesta());
            eventPublisher.publishEvent(new SolicitudRechazadaEvent(this, solicitud, decisionDTO.getMensajeRespuesta()));
        } else {
            throw new IllegalArgumentException("Acción no válida: " + decisionDTO.getAccion() + ". Debe ser ACEPTAR o RECHAZAR.");
        }

        SolicitudColaboracion guardada = solicitudColaboracionRepository.save(solicitud);
        SolicitudColaboracionResponseDTO responseDTO = convertToSolicitudResponseDTO(guardada, null, null);
        if (colaboracionCreada != null) {
            responseDTO.setColaboracionId(colaboracionCreada.getId());
        }
        return responseDTO;
    }

    @Override
    @Transactional
    public ColaboracionResponseDTO cancelarColaboracionEnCurso(UUID idColaboracion, String emailEmpleadoCancelador) {
        Empleado empleadoCancelador = getEmpleadoPorEmailUsuario(emailEmpleadoCancelador);
        Colaboracion colaboracion = colaboracionRepository.findById(idColaboracion)
                .orElseThrow(() -> new ResourceNotFoundException("Colaboración", "ID", idColaboracion.toString()));

        if (colaboracion.getEstado() != EstadoColaboracion.ACTIVA) {
            logger.warn("Intento de cancelar colaboración ID {} que no está ACTIVA. Estado actual: {}", idColaboracion, colaboracion.getEstado());
            throw new IllegalStateException("La colaboración con ID " + idColaboracion + " no está activa y no puede ser cancelada.");
        }

        if (!colaboracion.getEmpleadoA().getId().equals(empleadoCancelador.getId()) &&
                !colaboracion.getEmpleadoB().getId().equals(empleadoCancelador.getId())) {
            logger.warn("Intento no autorizado por '{}' para cancelar colaboración ID {} de la que no forma parte.", emailEmpleadoCancelador, idColaboracion);
            throw new AccessDeniedException("El empleado " + emailEmpleadoCancelador + " no forma parte de la colaboración ID: " + idColaboracion + " y no puede cancelarla.");
        }

        colaboracion.setEstado(EstadoColaboracion.INACTIVA);
        colaboracion.setFechaBloqueoCancelacion(LocalDateTime.now());

        Optional<PeriodoColaboracion> periodoACTIVAOpt = periodoColaboracionRepository.findByColaboracionAndFechaFinIsNull(colaboracion);
        periodoACTIVAOpt.ifPresent(periodo -> {
            periodo.setFechaFin(LocalDateTime.now());
            periodo.setEmpleadoFin(empleadoCancelador);
            periodoColaboracionRepository.save(periodo);
            logger.info("Periodo ID {} de colaboración ID {} finalizado.", periodo.getId(), idColaboracion);
        });

        Colaboracion guardada = colaboracionRepository.save(colaboracion);
        logger.info("Colaboración ID: {} cancelada exitosamente por '{}'.", idColaboracion, emailEmpleadoCancelador);
        eventPublisher.publishEvent(new ColaboracionCanceladaEvent(this, guardada, empleadoCancelador));

        return convertToColaboracionResponseDTO(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ColaboracionResponseDTO> listarColaboracionesActivas(String emailEmpleado, Pageable pageable) {
        Empleado empleado = getEmpleadoPorEmailUsuario(emailEmpleado);
        Page<Colaboracion> colaboracionesPage = colaboracionRepository.findColaboracionesActivasParaEmpleado(empleado, EstadoColaboracion.ACTIVA, pageable);

        if (colaboracionesPage.isEmpty()) {
            return Page.empty(pageable);
        }
        return colaboracionesPage.map(this::convertToColaboracionResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmpleadoSimpleDTO> buscarEmpleadosParaColaboracion(String emailEmpleadoSolicitante, String terminoBusqueda, Pageable pageable) {
        Empleado solicitante = getEmpleadoPorEmailUsuario(emailEmpleadoSolicitante);
        Page<Empleado> empleados;
        String terminoBusquedaNormalizado = (terminoBusqueda != null) ? terminoBusqueda.trim().toLowerCase() : null;

        if (terminoBusquedaNormalizado != null && !terminoBusquedaNormalizado.isEmpty()) {
            // Asume que EmpleadoRepository tiene un método para buscar por término y que no sea el ID del solicitante
            empleados = empleadoRepository.buscarPorTerminoExcluyendoId(terminoBusquedaNormalizado, solicitante.getId(), pageable);
        } else {
            // Listar todos los empleados excepto el solicitante
            empleados = empleadoRepository.findByIdNot(solicitante.getId(), pageable);
        }
        return empleados.map(emp -> modelMapper.map(emp, EmpleadoSimpleDTO.class));
    }

    private SolicitudColaboracionResponseDTO convertToSolicitudResponseDTO(SolicitudColaboracion solicitud, LocalDateTime fechaInicioProp, LocalDateTime fechaFinProp) {
        if (solicitud == null) return null;
        SolicitudColaboracionResponseDTO dto = modelMapper.map(solicitud, SolicitudColaboracionResponseDTO.class);

        Empleado emisor = solicitud.getEmpleadoEmisor();
        if (emisor != null) {
            dto.setIdEmpleadoEmisor(emisor.getId());
            dto.setNombreEmpleadoEmisor(emisor.getNombreCompleto());
            if (emisor.getUsuario() != null) {
                dto.setEmailEmpleadoEmisor(emisor.getUsuario().getEmail());
            }
        }
        Empleado receptor = solicitud.getEmpleadoReceptor();
        if (receptor != null) {
            dto.setIdEmpleadoReceptor(receptor.getId());
            dto.setNombreEmpleadoReceptor(receptor.getNombreCompleto());
            if (receptor.getUsuario() != null) {
                dto.setEmailEmpleadoReceptor(receptor.getUsuario().getEmail());
            }
        }
        if (solicitud.getColaboracion() != null) {
            dto.setColaboracionId(solicitud.getColaboracion().getId());
        }

        dto.setFechaInicioPeriodo(fechaInicioProp);
        dto.setFechaFinPeriodo(fechaFinProp);

        return dto;
    }

    private ColaboracionResponseDTO convertToColaboracionResponseDTO(Colaboracion colaboracion) {
        if (colaboracion == null) return null;
        ColaboracionResponseDTO dto = modelMapper.map(colaboracion, ColaboracionResponseDTO.class);
        Empleado empA = colaboracion.getEmpleadoA();
        if (empA != null) {
            dto.setIdEmpleadoA(empA.getId());
            dto.setNombreEmpleadoA(empA.getNombreCompleto());
            if (empA.getUsuario() != null) {
                dto.setEmailEmpleadoA(empA.getUsuario().getEmail());
            }
        }
        Empleado empB = colaboracion.getEmpleadoB();
        if (empB != null) {
            dto.setIdEmpleadoB(empB.getId());
            dto.setNombreEmpleadoB(empB.getNombreCompleto());
            if (empB.getUsuario() != null) {
                dto.setEmailEmpleadoB(empB.getUsuario().getEmail());
            }
        }

        List<PeriodoColaboracion> periodos = periodoColaboracionRepository.findByColaboracionOrderByFechaInicioDesc(colaboracion);
        if (periodos != null && !periodos.isEmpty()) {
            dto.setPeriodos(periodos.stream()
                    .map(this::convertToPeriodoColaboracionDTO)
                    .collect(Collectors.toList()));
        } else {
            dto.setPeriodos(Collections.emptyList());
        }
        return dto;
    }

    private PeriodoColaboracionDTO convertToPeriodoColaboracionDTO(PeriodoColaboracion periodo) {
        if (periodo == null) return null;
        PeriodoColaboracionDTO dto = modelMapper.map(periodo, PeriodoColaboracionDTO.class);
        Empleado empInicio = periodo.getEmpleadoInicio();
        if (empInicio != null) {
            dto.setEmpleadoInicioId(empInicio.getId());
            dto.setNombreEmpleadoInicio(empInicio.getNombreCompleto());
        }
        Empleado empFin = periodo.getEmpleadoFin();
        if (empFin != null) {
            dto.setEmpleadoFinId(empFin.getId());
            dto.setNombreEmpleadoFin(empFin.getNombreCompleto());
        }
        return dto;
    }
}
