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

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/administrador/nominas")
public class NominasController {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private NominaRepository nominaRepository;

    @Autowired
    private LineaNominaRepository lineaNominaRepository;

    // Método para manejar la raíz /administrador/nominas
    @GetMapping("")
    public String redirigirAlDashboard() {
        return "redirect:/administrador/nominas/dashboard";
    }

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

    // Método para mostrar el formulario de creación de nómina
    @GetMapping("/empleado/{id}/crear-nomina")
    public String mostrarFormularioCrearNomina(@PathVariable UUID id, Model model) {
        Empleado empleado = empleadoRepository.findById(id).orElseThrow();
        model.addAttribute("empleado", empleado);
        model.addAttribute("nomina", new Nomina());
        return "formulario-crear-nomina";
    }

    // Método para procesar la creación de la nómina
    @PostMapping("/empleado/{id}/crear-nomina")
    public String crearNomina(@PathVariable UUID id, @ModelAttribute Nomina nomina, Model model) {
        Empleado empleado = empleadoRepository.findById(id).orElseThrow();
        nomina.setEmpleado(empleado);
        nomina.setVersion(0);
        Nomina savedNomina = nominaRepository.saveAndFlush(nomina);
        System.out.println(empleado);
        System.out.println(savedNomina);
        return "redirect:/administrador/nominas/empleado/" + id + "/nominas";
    }

    // Método para mostrar el formulario de añadir línea de nómina
    @GetMapping("/nomina/{id}/aniadir-linea")
    public String mostrarFormularioAniadirLinea(@PathVariable UUID id, Model model) {
        Nomina nomina = nominaRepository.findById(id).orElseThrow();
        model.addAttribute("nomina", nomina);
        model.addAttribute("lineaNomina", new LineaNomina());
        return "formulario-aniadir-linea";
    }

    // Método para procesar la adición de línea de nómina
    @PostMapping("/nomina/{id}/aniadir-linea")
    public String aniaadirLineaNomina(@PathVariable UUID id, @ModelAttribute LineaNomina lineaNomina, Model model) {
        Nomina nomina = nominaRepository.findById(id).orElseThrow();
        lineaNomina.setNomina(nomina);
        Nomina refreshedNomina = nominaRepository.findById(id).orElseThrow();
        lineaNomina.setNomina(refreshedNomina);
        lineaNominaRepository.save(lineaNomina);
        return "redirect:/administrador/nominas/empleado/" + nomina.getEmpleado().getId() + "/nominas";
    }
}
