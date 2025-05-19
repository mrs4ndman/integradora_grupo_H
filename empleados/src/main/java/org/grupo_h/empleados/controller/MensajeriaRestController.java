package org.grupo_h.empleados.controller;

import org.grupo_h.comun.exceptions.ResourceNotFoundException;
import org.grupo_h.empleados.dto.mensajeria.MensajeRequestDTO;
import org.grupo_h.empleados.dto.mensajeria.MensajeResponseDTO;
import org.grupo_h.empleados.service.MensajeriaService;
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
 * Controlador REST para gestionar la mensajería instantánea entre empleados
 * dentro del contexto de una colaboración.
 */
@RestController
@RequestMapping("/api/empleados/mensajes")
public class MensajeriaRestController {

    private static final Logger logger = LoggerFactory.getLogger(MensajeriaRestController.class);

    private final MensajeriaService mensajeriaService;

    @Autowired
    public MensajeriaRestController(MensajeriaService mensajeriaService) {
        this.mensajeriaService = mensajeriaService;
    }

    /**
     * Obtiene el email del usuario autenticado desde la sesión HTTP.
     * @param session La sesión HTTP actual.
     * @return El email del usuario autenticado.
     * @throws ResponseStatusException con HttpStatus.UNAUTHORIZED si el email no se encuentra en la sesión.
     */
    private String getEmailAutenticado(HttpSession session) {
        String email = (String) session.getAttribute("emailAutenticado");
        if (email == null || email.isEmpty()) {
            logger.warn("API Acceso Mensajería: Intento de acceso no autenticado (falta 'emailAutenticado' en sesión). IP: {}", session.getAttribute("remoteAddr"));
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado. Por favor, inicie sesión.");
        }
        return email;
    }

    // --- FUNCIONALIDAD 8: Envío de un mensaje instantáneo y respuesta ---
    /**
     * Endpoint para enviar un nuevo mensaje dentro de una colaboración.
     * El emisor se determina a partir del usuario autenticado en la sesión.
     *
     * @param requestDTO DTO con los datos del mensaje (ID de colaboración, contenido, ID de mensaje a responder opcional).
     * @param session    Sesión HTTP para obtener el email del emisor.
     * @return ResponseEntity con el MensajeResponseDTO creado y estado HTTP 201 (CREATED).
     * @throws ResponseStatusException con HttpStatus.BAD_REQUEST, HttpStatus.FORBIDDEN, o HttpStatus.NOT_FOUND según el error.
     */
    @PostMapping
    public ResponseEntity<MensajeResponseDTO> enviarMensaje(
            @Valid @RequestBody MensajeRequestDTO requestDTO,
            HttpSession session) {
        String emailEmisor = getEmailAutenticado(session);
        logger.info("API [POST /mensajes]: Empleado '{}' enviando mensaje en colaboración ID: {}", emailEmisor, requestDTO.getIdColaboracion());
        try {
            MensajeResponseDTO response = mensajeriaService.enviarMensaje(requestDTO, emailEmisor);
            logger.info("API [POST /mensajes]: Mensaje ID '{}' enviado exitosamente por '{}' en colab ID '{}'", response.getId(), emailEmisor, requestDTO.getIdColaboracion());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.warn("API [POST /mensajes]: Error de negocio al enviar mensaje por '{}' en colab {}: {}", emailEmisor, requestDTO.getIdColaboracion(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (AccessDeniedException e) {
            logger.warn("API [POST /mensajes]: Acceso denegado al enviar mensaje por '{}' en colab {}: {}", emailEmisor, requestDTO.getIdColaboracion(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (ResourceNotFoundException e) {
            logger.warn("API [POST /mensajes]: Recurso no encontrado al enviar mensaje por '{}' en colab {}: {}", emailEmisor, requestDTO.getIdColaboracion(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // --- FUNCIONALIDAD 9: Listado de mensajes de una conversación ---
    /**
     * Endpoint para listar los mensajes de una colaboración específica.
     * El usuario autenticado debe ser parte de la colaboración.
     *
     * @param idColaboracion ID (UUID) de la colaboración cuyos mensajes se quieren listar.
     * @param session        Sesión HTTP para obtener el email del usuario actual.
     * @param pageable       Objeto de paginación y ordenación. Por defecto, ordena por fechaEmision descendente (más nuevos primero).
     * @return ResponseEntity con una página (Page) de MensajeResponseDTO.
     * @throws ResponseStatusException con HttpStatus.FORBIDDEN o HttpStatus.NOT_FOUND según el error.
     */
    @GetMapping("/colaboracion/{idColaboracion}")
    public ResponseEntity<Page<MensajeResponseDTO>> listarMensajesColaboracion(
            @PathVariable UUID idColaboracion,
            HttpSession session,
            @PageableDefault(size = 25, sort = "fechaEmision", direction = Sort.Direction.DESC) Pageable pageable) {
        String emailUsuarioActual = getEmailAutenticado(session);
        logger.debug("API [GET /colaboracion/{}]: Empleado '{}' listando mensajes. Paginación: {}", idColaboracion, emailUsuarioActual, pageable);
        try {
            Page<MensajeResponseDTO> pagina = mensajeriaService.listarMensajesColaboracion(idColaboracion, emailUsuarioActual, pageable);
            return ResponseEntity.ok(pagina);
        } catch (AccessDeniedException e) {
            logger.warn("API [GET /colaboracion/{}]: Acceso denegado para '{}': {}", idColaboracion, emailUsuarioActual, e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (ResourceNotFoundException e) {
            logger.warn("API [GET /colaboracion/{}]: Recurso no encontrado para '{}': {}", idColaboracion, emailUsuarioActual, e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
