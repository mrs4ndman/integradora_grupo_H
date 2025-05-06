package org.grupo_h.administracion.controller;

import org.grupo_h.comun.entity.Administrador;
import org.grupo_h.administracion.dto.AdministradorRegistroDTO;
import org.grupo_h.administracion.service.AdministradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para gestionar las operaciones relacionadas con los administradores.
 */
@RestController
@RequestMapping("/api/administrador")
public class AdministradorRestController {

    private final AdministradorService administradorService;

    /**
     * Constructor para inyectar el servicio de administradores.
     *
     * @param administradorService Servicio de administradores.
     */
    @Autowired
    public AdministradorRestController(AdministradorService administradorService) {
        this.administradorService = administradorService;
    }

    // TODO: MOVER TODO LO POSIBLE A ENDPOINTS REST

}
