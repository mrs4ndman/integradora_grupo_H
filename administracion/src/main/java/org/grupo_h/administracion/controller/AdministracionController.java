package org.grupo_h.administracion.controller;

import org.grupo_h.administracion.service.EmpleadoService;
import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.administracion.repository.EmpleadoRepository;
import org.grupo_h.administracion.dto.EmpleadoDetalleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("administracion")
public class AdministracionController {

    @Autowired
    private EmpleadoService empleadoService;

    @GetMapping("/detalle/{id}")
    public String obtenerDetalleEmpleado(@PathVariable UUID id, Model model) {
        Optional<EmpleadoDetalleDTO> empleadoOpt = empleadoService.obtenerDetalleEmpleado(id);
        if (empleadoOpt.isPresent()) {
            model.addAttribute("empleado", empleadoOpt.get());
            return "detalleEmpleado"; // Plantilla Thymeleaf
        } else {
            return "detalleEmpleado"; // Página de error
        }
    }
}
