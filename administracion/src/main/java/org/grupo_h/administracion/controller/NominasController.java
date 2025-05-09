package org.grupo_h.administracion.controller;

import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.comun.entity.Nomina;
import org.grupo_h.comun.entity.LineaNomina;
import org.grupo_h.comun.repository.EmpleadoRepository;
import org.grupo_h.comun.repository.NominaRepository;
import org.grupo_h.comun.repository.LineaNominaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

@Controller
@RequestMapping("/administrador/nominas")
public class NominasController {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private NominaRepository nominaRepository;

    // Método para mostrar el dashboard de búsqueda de empleados
    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model) {
        return "dashboardNominas";
    }

    // Método para buscar empleados por nombre o apellido
    @GetMapping("/buscar")
    public String buscarEmpleados(@RequestParam String criterio, Model model) {
        List<Empleado> empleados = empleadoRepository.findByNombreContainingOrApellidosContaining(criterio, criterio);
        model.addAttribute("empleados", empleados);
        return "dashboardNominas";
    }

    // Método para listar las nóminas de un empleado específico
    @GetMapping("/empleado/{id}/nominas")
    public String listarNominasEmpleado(@PathVariable UUID id, Model model) {
        Empleado empleado = empleadoRepository.findById(id).orElseThrow();
        model.addAttribute("empleado", empleado);
        model.addAttribute("nominas", empleado.getNominas());
        return "empleado-nominas";
    }
}
