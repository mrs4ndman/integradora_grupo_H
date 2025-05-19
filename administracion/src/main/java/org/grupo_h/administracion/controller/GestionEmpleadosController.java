package org.grupo_h.administracion.controller;

import jakarta.persistence.EntityNotFoundException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size,
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

//        List<EmpleadoDTO> resultados = empleadoService.buscarEmpleados(filtro);
        Page<EmpleadoDTO> empleadosPage = empleadoService
                .buscarEmpleadosPaginados(filtro, page, size);

        model.addAttribute("resultados", empleadosPage.getContent());
        model.addAttribute("currentPage", empleadosPage.getNumber());
        model.addAttribute("totalPages", empleadosPage.getTotalPages());
        model.addAttribute("totalItems", empleadosPage.getTotalElements());


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
            redirectAttributes.addFlashAttribute("error", "Empleado no encontrado en el get.");
            return "redirect:/administrador/consulta-empleado";
        }

        Empleado empleado = empleadoOpt.get();

        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setNombre(empleado.getNombre());
        dto.setApellidos(empleado.getApellidos());
        dto.setEdad(empleado.getEdad());
        dto.setNumeroDni(empleado.getNumeroDocumento());
        dto.setNombreDepartamento(empleado.getDepartamento().getNombreDept());

        // **Convertir foto a Base64** (si existe)
        if (empleado.getFotografia() != null && empleado.getFotografia().length > 0) {
            String b64 = Base64.getEncoder().encodeToString(empleado.getFotografia());
            dto.setFotoBase64("data:image/jpeg;base64," + b64);
        }

        model.addAttribute("empleado", dto);
        model.addAttribute("departamentos", departamentoService.obtenerTodosDepartamentos());

        return "edicion-empleado";
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
                                          RedirectAttributes redirectAttributes,
                                          @RequestParam(value = "fotografiaDTO", required = false) MultipartFile multipartFile) {

        Optional<Empleado> empleadoOpt = empleadoService.buscarPorDni(dto.getNumeroDni());
        System.err.println(empleadoOpt);
        if (empleadoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Empleado no encontrado en el post.");
            return "redirect:/administrador/consulta-empleado";
        }

        Empleado empleado = empleadoOpt.get();
        empleado.setNombre(dto.getNombre());
        empleado.setApellidos(dto.getApellidos());

        if (dto.getEdad() != null) {
            empleado.setEdad(dto.getEdad());
        } else {
            System.out.println("Edad es null. Se omite.");
        }
        try {
            Departamento departamento = (Departamento) departamentoRepository.findByNombreDept(dto.getNombreDepartamento()).orElse(null);
            empleado.setDepartamento(departamento);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

//        if(!multipartFile.isEmpty()){
//            dto.setFotografiaArchivo(multipartFile.getBytes());
//        }else{
//            dto.setFotografiaArchivo(null);
//        }

        System.out.println("AQUI → " + empleado);

        try{
        empleadoRepository.save(empleado);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }


        redirectAttributes.addFlashAttribute("exito", "Empleado actualizado correctamente.");
        return "redirect:/administrador/consulta-empleado";
    }

    @PostMapping("/borrar-empleado/{dni}")
    public String borrarEmpleadoPorDni(@PathVariable String dni,
                                       RedirectAttributes attrs,
                                       HttpSession session) {
        if (session.getAttribute("emailAutenticadoAdmin") == null) {
            attrs.addFlashAttribute("error", "Debes iniciar sesión.");
            return "redirect:/administrador/inicio-sesion";
        }
        try {
            empleadoService.eliminarPorDni(dni);
            attrs.addFlashAttribute("exito", "Empleado borrado correctamente.");
        } catch (EntityNotFoundException e) {
            attrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/administrador/consulta-empleado";
    }

    @GetMapping("/gestion-estado-empleados")
    public String mostrarGestionEstadoEmpleados(
            @RequestParam(name = "searchTerm", required = false) String searchTerm,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model, HttpSession session, RedirectAttributes redirectAttributes) {

        if (session.getAttribute("emailAutenticadoAdmin") == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para acceder a esta página.");
            return "redirect:/administrador/inicio-sesion";
        }

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("nombre").ascending());
        Page<EmpleadoDTO> paginaEmpleados = empleadoService.getEmpleadosParaGestionEstado(searchTerm, pageable);

        model.addAttribute("paginaEmpleados", paginaEmpleados);
        model.addAttribute("searchTerm", searchTerm);

        int totalPages = paginaEmpleados.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "gestionEstadoEmpleados";
    }

    @PostMapping("/dar-baja-empleado/{id}")
    public String darBajaEmpleado(@PathVariable("id") UUID id,
                                  RedirectAttributes redirectAttributes,
                                  HttpSession session) {
        if (session.getAttribute("emailAutenticadoAdmin") == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión.");
            return "redirect:/administrador/inicio-sesion";
        }
        try {
            empleadoService.darDeBajaLogicaEmpleado(id);
            redirectAttributes.addFlashAttribute("exito", "Empleado dado de baja correctamente.");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Empleado no encontrado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al dar de baja al empleado: " + e.getMessage());
        }
        return "redirect:/administrador/gestion-estado-empleados";
    }

    @PostMapping("/reactivar-empleado/{id}")
    public String reactivarEmpleado(@PathVariable("id") UUID id,
                                    RedirectAttributes redirectAttributes,
                                    HttpSession session) {
        if (session.getAttribute("emailAutenticadoAdmin") == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión.");
            return "redirect:/administrador/inicio-sesion";
        }
        try {
            empleadoService.reactivarEmpleado(id);
            redirectAttributes.addFlashAttribute("exito", "Empleado reactivado correctamente.");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Empleado no encontrado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al reactivar al empleado: " + e.getMessage());
        }
        return "redirect:/administrador/gestion-estado-empleados";
    }

}
