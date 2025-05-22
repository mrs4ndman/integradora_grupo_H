package org.grupo_h.empleados.controller;

import org.grupo_h.empleados.dto.NominaEmpleadoResumenDTO;
import org.grupo_h.empleados.service.NominaEmpleadoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/empleados/mis-nominas")
public class NominaEmpleadoRestController {

    private static final Logger logger = LoggerFactory.getLogger(NominaEmpleadoRestController.class);
    private final NominaEmpleadoService nominaEmpleadoService;

    @Autowired
    public NominaEmpleadoRestController(NominaEmpleadoService nominaEmpleadoService) {
        this.nominaEmpleadoService = nominaEmpleadoService;
    }

    private String getEmailAutenticado(HttpSession session) {
        String email = (String) session.getAttribute("emailAutenticado");
        if (email == null || email.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado.");
        }
        return email;
    }

    @GetMapping
    public ResponseEntity<Page<NominaEmpleadoResumenDTO>> consultarMisNominas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @PageableDefault(size = 10, sort = "fechaInicio", direction = Sort.Direction.DESC) Pageable pageable,
            HttpSession session) {
        try {
            String emailEmpleado = getEmailAutenticado(session);
            logger.info("API: Empleado {} consultando sus nóminas. Filtros: fechaDesde={}, fechaHasta={}, pageable={}",
                    emailEmpleado, fechaDesde, fechaHasta, pageable);

            Page<NominaEmpleadoResumenDTO> paginaNominas = nominaEmpleadoService.consultarNominasPorEmpleado(
                    emailEmpleado, fechaDesde, fechaHasta, pageable
            );
            return ResponseEntity.ok(paginaNominas);
        } catch (AccessDeniedException e) {
            logger.warn("API: Acceso denegado al consultar nóminas: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            logger.error("API: Error al consultar nóminas: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al procesar la solicitud de nóminas.");
        }
    }
}