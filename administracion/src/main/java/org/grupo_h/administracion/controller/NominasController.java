package org.grupo_h.administracion.controller;

import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.comun.entity.Nomina;
import org.grupo_h.comun.entity.LineaNomina;
import org.grupo_h.comun.repository.EmpleadoRepository;
import org.grupo_h.comun.repository.LineaNominaRepository;
import org.grupo_h.comun.repository.NominaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@Controller
@RequestMapping("/administrador/nominas")
public class NominasController {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private LineaNominaRepository lineaNominaRepository;

    @Autowired
    private NominaRepository nominaRepository;

    // Ya no necesitas LineaNominaRepository aquí, cascada en Nomina se encarga

    @GetMapping("")
    public String redirigirAlDashboard() {
        return "redirect:/administrador/nominas/dashboard";
    }

    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model) {
        return "dashboardNominas";
    }

    @GetMapping("/buscar")
    public String buscarEmpleados(@RequestParam String criterio, Model model) {
        model.addAttribute("empleados",
                empleadoRepository.findByNombreContainingOrApellidosContaining(criterio, criterio));
        return "dashboardNominas";
    }

    @GetMapping("/empleado/{id}/nominas")
    public String listarNominasEmpleado(@PathVariable UUID id, Model model) {
        Empleado empleado = empleadoRepository.findById(id).orElseThrow();
        model.addAttribute("empleado", empleado);
        model.addAttribute("nominas", empleado.getNominas());
        return "empleado-nominas";
    }

    @GetMapping("/detalle/{id}")
    public String mostrarDetalleNomina(@PathVariable UUID id, Model model) {
        Nomina nomina = nominaRepository.findById(id).orElseThrow();
        model.addAttribute("nomina", nomina);
        return "detalle-nomina";
    }

    @GetMapping("/empleado/{id}/crear-nomina")
    public String mostrarFormularioCrearNomina(@PathVariable UUID id, Model model) {
        Empleado empleado = empleadoRepository.findById(id).orElseThrow();
        model.addAttribute("empleado", empleado);
        return "formulario-crear-nomina";
    }

    @PostMapping("/empleado/{id}/crear-nomina")
    @Transactional
    public String crearNomina(@PathVariable UUID id,
                              @RequestParam("fechaInicio") String fechaInicio,
                              @RequestParam("fechaFin") String fechaFin) {
        Empleado empleado = empleadoRepository.findById(id).orElseThrow();
        Nomina nomina = new Nomina();
        // No fijamos id ni version: Hibernate los generará
        nomina.setFechaInicio(LocalDate.parse(fechaInicio));
        nomina.setFechaFin(LocalDate.parse(fechaFin));
        nomina.setEmpleado(empleado);
        // Inicializar la lista si es null
        if (nomina.getLineas() != null) {
            nomina.getLineas().forEach(linea -> linea.setNomina(nomina));
        }
        nominaRepository.save(nomina);
        return "redirect:/administrador/nominas/empleado/" + id + "/nominas";
    }

    @PostMapping("/nomina/{id}/borrar")
    @Transactional
    public String borrarNomina(@PathVariable UUID id) {
        Nomina nomina = nominaRepository.findById(id).orElseThrow();
        UUID empleadoId = nomina.getEmpleado().getId();
        nominaRepository.delete(nomina);
        return "redirect:/administrador/nominas/empleado/" + empleadoId + "/nominas";
    }

    @GetMapping("/nomina/{id}/aniadir-linea")
    public String mostrarFormularioAniadirLinea(@PathVariable UUID id, Model model) {
        Nomina nomina = nominaRepository.findById(id).orElseThrow();
        model.addAttribute("nomina", nomina);
        return "formulario-aniadir-linea";
    }

    @PostMapping("/nomina/{id}/aniadir-linea")
    @Transactional
    public String aniadirLineaNomina(@PathVariable UUID id,
                                     @RequestParam("concepto") String concepto,
                                     @RequestParam("cantidad") int cantidad,
                                     @RequestParam("importe") Double importe) {
        Nomina nomina = nominaRepository.findById(id).orElseThrow();
        LineaNomina lineaNomina = new LineaNomina();
        lineaNomina.setConcepto(concepto);
        lineaNomina.setCantidad(cantidad);
        lineaNomina.setImporte(importe);
        lineaNomina.setNomina(nomina);
        nomina.getLineas().add(lineaNomina);
        // Al hacer save(nomina) con cascade=ALL, Hibernate insertará la nueva línea
        nominaRepository.save(nomina);
        return "redirect:/administrador/nominas/empleado/"
                + nomina.getEmpleado().getId() + "/nominas";
    }

    @PostMapping("/linea/{id}/borrar")
    @Transactional
    public String borrarLineaNomina(@PathVariable UUID id) {
        LineaNomina lineaNomina = lineaNominaRepository.findById(id).orElseThrow();
        UUID nominaId = lineaNomina.getNomina().getId();
        lineaNominaRepository.delete(lineaNomina);
        return "redirect:/administrador/nominas/detalle/" + nominaId;
    }
}
