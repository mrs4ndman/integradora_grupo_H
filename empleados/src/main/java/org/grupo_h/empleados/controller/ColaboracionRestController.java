package org.grupo_h.empleados.controller;

import org.grupo_h.comun.exceptions.ResourceNotFoundException;
import org.grupo_h.empleados.dto.colaboracion.ColaboracionResponseDTO;
import org.grupo_h.empleados.dto.colaboracion.DecisionSolicitudRequestDTO;
import org.grupo_h.empleados.dto.colaboracion.SolicitudColaboracionRequestDTO;
import org.grupo_h.empleados.dto.colaboracion.SolicitudColaboracionResponseDTO;
import org.grupo_h.empleados.dto.EmpleadoSimpleDTO; // Para el resultado de la búsqueda
import org.grupo_h.empleados.service.ColaboracionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.UUID;

/**
 * Controlador REST para gestionar las colaboraciones entre empleados.
 * Proporciona endpoints para crear, listar y gestionar solicitudes de colaboración,
 * así como para cancelar colaboraciones activas y buscar empleados.
 * La autenticación se basa en el atributo "emailAutenticado" en la HttpSession.
 */
@RestController
@RequestMapping("/api/empleados/colaboraciones")
public class ColaboracionRestController {

    private static final Logger logger = LoggerFactory.getLogger(ColaboracionRestController.class);

    private final ColaboracionService colaboracionService;

    @Autowired
    public ColaboracionRestController(ColaboracionService colaboracionService) {
        this.colaboracionService = colaboracionService;
    }

    private String getEmailAutenticado(HttpSession session) {
        String email = (String) session.getAttribute("emailAutenticado");
        if (email == null || email.isEmpty()) {
            logger.warn("API Acceso Colaboraciones: Intento de acceso no autenticado (falta 'emailAutenticado' en sesión).");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado. Por favor, inicie sesión.");
        }
        return email;
    }

    /**
     * Endpoint para buscar empleados disponibles para iniciar una colaboración.
     * Excluye al empleado que realiza la búsqueda.
     * @param session Sesión HTTP para identificar al solicitante.
     * @param terminoBusqueda (Opcional) Término para filtrar por nombre, apellidos o email.
     * @param pageable Objeto de paginación.
     * @return ResponseEntity con una página de EmpleadoSimpleDTO.
     */
    @GetMapping("/empleados-disponibles")
    public ResponseEntity<Page<EmpleadoSimpleDTO>> buscarEmpleadosParaColaboracion(
            HttpSession session,
            @RequestParam(value = "buscar", required = false) String terminoBusqueda,
            @PageableDefault(size = 10, sort = "apellidos", direction = Sort.Direction.ASC) Pageable pageable) {
        String emailEmpleadoSolicitante = getEmailAutenticado(session);
        logger.debug("API [GET /empleados-disponibles]: Empleado '{}' buscando empleados. Término: '{}', Paginación: {}", emailEmpleadoSolicitante, terminoBusqueda, pageable);
        try {
            Page<EmpleadoSimpleDTO> empleados = colaboracionService.buscarEmpleadosParaColaboracion(emailEmpleadoSolicitante, terminoBusqueda, pageable);
            return ResponseEntity.ok(empleados);
        } catch (ResourceNotFoundException e) {
            logger.error("API [GET /empleados-disponibles]: Error crítico, el empleado solicitante '{}' no fue encontrado: {}", emailEmpleadoSolicitante, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al procesar la búsqueda de empleados debido a un problema con el usuario solicitante.");
        }
    }

    @PostMapping("/solicitudes")
    public ResponseEntity<SolicitudColaboracionResponseDTO> crearSolicitud(
            @Valid @RequestBody SolicitudColaboracionRequestDTO requestDTO,
            HttpSession session) {
        String emailEmpleadoEmisor = getEmailAutenticado(session);
        logger.info("API [POST /solicitudes]: Empleado '{}' creando solicitud para empleado con email '{}'", emailEmpleadoEmisor, requestDTO.getEmailEmpleadoDestino());
        try {
            SolicitudColaboracionResponseDTO response = colaboracionService.crearSolicitud(requestDTO, emailEmpleadoEmisor);
            logger.info("API [POST /solicitudes]: Solicitud ID '{}' creada exitosamente por '{}'", response.getId(), emailEmpleadoEmisor);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.warn("API [POST /solicitudes]: Error de negocio al crear solicitud para '{}': {}", emailEmpleadoEmisor, e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (ResourceNotFoundException e) {
            logger.warn("API [POST /solicitudes]: Recurso no encontrado al crear solicitud para '{}': {}", emailEmpleadoEmisor, e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/solicitudes/emitidas")
    public ResponseEntity<Page<SolicitudColaboracionResponseDTO>> listarSolicitudesEmitidas(
            HttpSession session,
            @PageableDefault(size = 10, sort = "fechaSolicitud", direction = Sort.Direction.DESC) Pageable pageable) {
        String emailEmpleado = getEmailAutenticado(session);
        logger.debug("API [GET /solicitudes/emitidas]: Empleado '{}' listando sus solicitudes emitidas. Paginación: {}", emailEmpleado, pageable);
        Page<SolicitudColaboracionResponseDTO> pagina = colaboracionService.listarSolicitudesEmitidas(emailEmpleado, pageable);
        return ResponseEntity.ok(pagina);
    }

    @GetMapping("/solicitudes/recibidas")
    public ResponseEntity<Page<SolicitudColaboracionResponseDTO>> listarSolicitudesRecibidas(
            HttpSession session,
            @RequestParam(value = "estado", required = false) String estado,
            @PageableDefault(size = 10, sort = "fechaSolicitud", direction = Sort.Direction.DESC) Pageable pageable) {
        String emailEmpleado = getEmailAutenticado(session);
        logger.debug("API [GET /solicitudes/recibidas]: Empleado '{}' listando sus solicitudes recibidas (estado: {}). Paginación: {}", emailEmpleado, estado, pageable);
        Page<SolicitudColaboracionResponseDTO> pagina;
        if ("PENDING".equalsIgnoreCase(estado)) {
            pagina = colaboracionService.listarSolicitudesPendientesRecibidas(emailEmpleado, pageable);
        } else {
            pagina = colaboracionService.listarSolicitudesRecibidas(emailEmpleado, pageable);
        }
        return ResponseEntity.ok(pagina);
    }

    @GetMapping("/solicitudes/recibidas/pendientes/conteo")
    public ResponseEntity<Long> contarSolicitudesPendientesRecibidas(HttpSession session) {
        String emailEmpleado = getEmailAutenticado(session);
        logger.debug("API [GET /solicitudes/recibidas/pendientes/conteo]: Empleado '{}' obteniendo conteo de solicitudes pendientes.", emailEmpleado);
        long conteo = colaboracionService.contarSolicitudesPendientesRecibidas(emailEmpleado);
        return ResponseEntity.ok(conteo);
    }

    @PutMapping("/solicitudes/{idSolicitud}")
    public ResponseEntity<SolicitudColaboracionResponseDTO> gestionarSolicitud(
            @PathVariable UUID idSolicitud,
            @Valid @RequestBody DecisionSolicitudRequestDTO decisionDTO,
            HttpSession session) {
        String emailEmpleadoReceptor = getEmailAutenticado(session);
        logger.info("API [PUT /solicitudes/{}]: Empleado '{}' gestionando solicitud con acción: {}", idSolicitud, emailEmpleadoReceptor, decisionDTO.getAccion());
        try {
            SolicitudColaboracionResponseDTO response = colaboracionService.gestionarSolicitud(idSolicitud, decisionDTO, emailEmpleadoReceptor);
            logger.info("API [PUT /solicitudes/{}]: Solicitud gestionada exitosamente por '{}'", idSolicitud, emailEmpleadoReceptor);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.warn("API [PUT /solicitudes/{}]: Error de negocio al gestionar para '{}': {}", idSolicitud, emailEmpleadoReceptor, e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (AccessDeniedException e) {
            logger.warn("API [PUT /solicitudes/{}]: Acceso denegado para '{}': {}", idSolicitud, emailEmpleadoReceptor, e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (ResourceNotFoundException e) {
            logger.warn("API [PUT /solicitudes/{}]: Recurso no encontrado para '{}': {}", idSolicitud, emailEmpleadoReceptor, e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{idColaboracion}/cancelar")
    public ResponseEntity<ColaboracionResponseDTO> cancelarColaboracion(
            @PathVariable UUID idColaboracion,
            HttpSession session) {
        String emailEmpleadoCancelador = getEmailAutenticado(session);
        logger.info("API [PUT /{}/cancelar]: Empleado '{}' cancelando colaboración.", idColaboracion, emailEmpleadoCancelador);
        try {
            ColaboracionResponseDTO response = colaboracionService.cancelarColaboracionEnCurso(idColaboracion, emailEmpleadoCancelador);
            logger.info("API [PUT /{}/cancelar]: Colaboración cancelada exitosamente por '{}'", idColaboracion, emailEmpleadoCancelador);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            logger.warn("API [PUT /{}/cancelar]: Error de estado para '{}': {}", idColaboracion, emailEmpleadoCancelador, e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (AccessDeniedException e) {
            logger.warn("API [PUT /{}/cancelar]: Acceso denegado para '{}': {}", idColaboracion, emailEmpleadoCancelador, e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (ResourceNotFoundException e) {
            logger.warn("API [PUT /{}/cancelar]: Recurso no encontrado para '{}': {}", idColaboracion, emailEmpleadoCancelador, e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/activas")
    public ResponseEntity<Page<ColaboracionResponseDTO>> listarColaboracionesActivas(
            HttpSession session,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        String emailEmpleado = getEmailAutenticado(session);
        logger.debug("API [GET /activas]: Empleado '{}' listando sus colaboraciones activas. Paginación: {}", emailEmpleado, pageable);
        Page<ColaboracionResponseDTO> pagina = colaboracionService.listarColaboracionesActivas(emailEmpleado, pageable);
        return ResponseEntity.ok(pagina);
    }
}
