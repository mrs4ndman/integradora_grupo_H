package org.grupo_h.administracion.controller;

import org.grupo_h.administracion.service.EmpleadoService;
import org.grupo_h.administracion.dto.EmpleadoDetalleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;
import java.util.UUID;

/**
 * Controlador encargado de gestionar las operaciones de administración
 * relacionadas con empleados y otros recursos administrativos.
 * <p>
 * Este controlador proporciona endpoints para la gestión y visualización
 * de información relacionada con los empleados desde el módulo de administración.
 * </p>
 *
 * @author Grupo H
 * @version 1.0
 */
@Controller
@RequestMapping("administracion")
public class AdministracionController {

    /**
     * Servicio que proporciona la lógica de negocio para la gestión de empleados.
     */
    @Autowired
    private EmpleadoService empleadoService;

    /**
     * Obtiene y muestra el detalle de un empleado específico.
     *
     * @param id    Identificador único del empleado a consultar
     * @param model Modelo para transferir datos a la vista
     * @return Nombre de la vista a renderizar (detalleEmpleado)
     */
    @GetMapping("/detalle/{id}")
    public String obtenerDetalleEmpleado(@PathVariable UUID id, Model model) {
        Optional<EmpleadoDetalleDTO> empleadoOpt = empleadoService.obtenerDetalleEmpleado(id);
        if (empleadoOpt.isPresent()) {
            model.addAttribute("empleado", empleadoOpt.get());
            return "detalleEmpleado"; // Plantilla Thymeleaf
        } else {
            // TODO: Crear una pagina de error
            return "detalleEmpleado"; // Página de error
        }
    }
}
