package org.grupo_h.administracion.controller;

import jakarta.servlet.http.HttpSession;
import org.grupo_h.administracion.dto.EmpleadoConsultaDTO;
import org.grupo_h.administracion.dto.EmpleadoDTO;
import org.grupo_h.administracion.service.EmpleadoService;
import org.grupo_h.comun.entity.Departamento;
import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.comun.repository.DepartamentoRepository;
import org.grupo_h.comun.repository.EmpleadoRepository;
import org.grupo_h.comun.service.DepartamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/administrador")
public class GestionEmpleadosController {
    @Autowired
    DepartamentoService departamentoService;

    @Autowired
    EmpleadoService empleadoService;

    @Autowired
    DepartamentoRepository departamentoRepository;

    @Autowired
    EmpleadoRepository empleadoRepository;


    /* ------------------------ GESTION DE EMPLEADOS ----------------------------- */
    /* ------------------------ Búsqueda Parametrizada de Empleados ----------------------------- */

    /**
     * Muestra la página de consulta parametrizada de empleados con el formulario de filtros
     * y resultados de la búsqueda si hay parámetros establecidos
     *
     * @param filtro             Objeto que contiene los criterios de búsqueda
     * @param session            Sesión HTTP para validar la autenticación
     * @param redirectAttributes Para enviar mensajes entre redirecciones
     * @param model              Modelo para pasar datos a la vista
     * @return La vista de consulta parametrizada o redirección al login si no hay sesión
     */
    @GetMapping("/consulta-empleado")
    public String consultaEmpleado(@ModelAttribute("filtro") EmpleadoConsultaDTO filtro,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {

        if (session.getAttribute("emailAutenticadoAdmin") == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para acceder a esta página.");
            System.out.println("Me sacó");
            return "redirect:/administrador/inicio-sesion";
        }

        System.out.println("Estoy en el POST de Consulta");
        System.out.println("DNI recibido en el filtro: " + filtro.getNumeroDni());


        List<Departamento> departamentos = departamentoService.obtenerTodosDepartamentos();
        model.addAttribute("departamentos", departamentos);

        List<EmpleadoDTO> resultados = empleadoService.buscarEmpleados(filtro);

        model.addAttribute("resultados", resultados);
        return "consultaParametrizadaEmpleado";
    }

    /**
     * Procesa la solicitud de búsqueda de empleados según los criterios especificados en el filtro
     *
     * @param filtro             Objeto que contiene los criterios de búsqueda
     * @param session            Sesión HTTP para validar la autenticación
     * @param redirectAttributes Para enviar mensajes entre redirecciones
     * @param model              Modelo para pasar datos a la vista
     * @return La vista de consulta parametrizada o redirección al login si no hay sesión
     */
    @PostMapping("/consulta-empleado")
    public String procesarBusquedaEmpleado(@ModelAttribute("filtro") EmpleadoConsultaDTO filtro,
                                           HttpSession session,
                                           RedirectAttributes redirectAttributes,
                                           Model model) {

        if (session.getAttribute("emailAutenticadoAdmin") == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para acceder a esta página.");
            System.out.println("Me sacó");
            return "redirect:/administrador/inicio-sesion";
        }

        System.out.println("Estoy en el POST de Consulta");
        System.out.println("DNI recibido en el filtro: " + filtro.getNumeroDni());


        List<Departamento> departamentos = departamentoService.obtenerTodosDepartamentos();
        model.addAttribute("departamentos", departamentos);

        List<EmpleadoDTO> resultados = empleadoService.buscarEmpleados(filtro);

        model.addAttribute("resultados", resultados);


        return "consultaParametrizadaEmpleado";
    }

    /**
     * Muestra el formulario de edición de un empleado específico identificado por su DNI
     *
     * @param dni                DNI del empleado a editar
     * @param model              Modelo para pasar datos a la vista
     * @param redirectAttributes Para enviar mensajes entre redirecciones
     * @param session            Sesión HTTP para validar la autenticación
     * @return La vista de edición de empleado o redirección si no se encuentra o no hay sesión
     */
    @GetMapping("/editar-empleado/{dni}")
    public String mostrarFormularioEdicion(@PathVariable("dni") String dni,
                                           Model model,
                                           RedirectAttributes redirectAttributes,
                                           HttpSession session) {
        System.err.println("Estoy en editar empleado");

        if (session.getAttribute("emailAutenticadoAdmin") == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para acceder a esta página.");
            System.out.println("Me sacó");
            return "redirect:/administrador/inicio-sesion";
        }

        Optional<Empleado> empleadoOpt = empleadoService.buscarPorDni(dni);

        if (empleadoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Empleado no encontrado.");
            return "redirect:/administrador/consulta-empleado";
        }

        Empleado empleado = empleadoOpt.get();

        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setNombre(empleado.getNombre());
        dto.setApellido(empleado.getApellidos());
        dto.setEdad(empleado.getEdad());
        dto.setNumeroDni(empleado.getNumeroDocumento());
        dto.setNombreDepartamento(empleado.getDepartamento().getNombreDept());
        // Agrega los campos que quieras editar

        model.addAttribute("empleado", dto);
        model.addAttribute("departamentos", departamentoService.obtenerTodosDepartamentos());

        return "edicion-empleado"; // Nombre del HTML/Thymeleaf para editar
    }

    /**
     * Procesa la edición de un empleado con los datos actualizados
     *
     * @param dto                Objeto con los datos actualizados del empleado
     * @param redirectAttributes Para enviar mensajes entre redirecciones
     * @return Redirección a la página de consulta de empleados con mensaje de éxito o error
     */
    @PostMapping("/editar-empleado")
    public String procesarEdicionEmpleado(@ModelAttribute("empleado") EmpleadoDTO dto,
                                          RedirectAttributes redirectAttributes) {

        Optional<Empleado> empleadoOpt = empleadoService.buscarPorDni(dto.getNumeroDni());
        if (empleadoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Empleado no encontrado.");
            return "redirect:/administrador/consulta-empleado";
        }

        Empleado empleado = empleadoOpt.get();
        empleado.setNombre(dto.getNombre());
        empleado.setApellidos(dto.getApellido());
        empleado.setEdad(dto.getEdad());
        try {
            Departamento departamento = (Departamento) departamentoRepository.findByNombreDept(dto.getNombreDepartamento()).orElse(null);
            empleado.setDepartamento(departamento);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("AQUI → " + empleado);
        empleadoRepository.save(empleado);


        // Guarda los cambios

        redirectAttributes.addFlashAttribute("exito", "Empleado actualizado correctamente.");
        return "redirect:/administrador/consulta-empleado";
    }
}
