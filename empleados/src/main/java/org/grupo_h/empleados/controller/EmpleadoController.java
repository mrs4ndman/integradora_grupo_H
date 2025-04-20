package org.grupo_h.empleados.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.comun.entity.Usuario;
import org.grupo_h.empleados.dto.CuentaCorrienteDTO;
import org.grupo_h.empleados.dto.DireccionDTO;
import org.grupo_h.empleados.dto.EmpleadoDetalleDTO;
import org.grupo_h.empleados.dto.EmpleadoRegistroDTO;
import org.grupo_h.empleados.repository.EmpleadoRepository;
import org.grupo_h.empleados.service.EmpleadoService;
import org.grupo_h.empleados.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("empleados")
public class EmpleadoController {
    @Autowired
    private final EmpleadoService empleadoService;

    @Autowired
    private final EmpleadoRepository empleadoRepository;

    public EmpleadoController(EmpleadoService empleadoService, EmpleadoRepository empleadoRepository) {
        this.empleadoService = empleadoService;
        this.empleadoRepository = empleadoRepository;
    }

    // Instanciar objeto EmpleadoDTO
    @ModelAttribute("empleadoRegistroDTO")
    private EmpleadoRegistroDTO getEmpleadoDTO(HttpSession session) {
        // Accedemos a la sesión
        EmpleadoRegistroDTO empleadoRegistroDTO = (EmpleadoRegistroDTO) session.getAttribute("empleadoRegistroDTO");
        // Si hay null
        if (empleadoRegistroDTO == null) {
            // Instancia el objeto
            empleadoRegistroDTO = new EmpleadoRegistroDTO();
            // Añade el atributo del objeto a la sesión de dicho usuario
            session.setAttribute("empleadoRegistroDTO", empleadoRegistroDTO);
        }
        return empleadoRegistroDTO;
    }




    // Muestra el formulario de registro
    @GetMapping("/registro-datos")
    public String mostrarFormularioRegistro( @ModelAttribute EmpleadoRegistroDTO empleadoRegistroDTO,
                                             HttpSession session) {
        session.setAttribute("empleadoRegistroDTO", empleadoRegistroDTO);
        return "empleadoRegistro"; // corresponde a empleadoRegistro.html
    }

    // Procesa el envío del formulario de registro
    @PostMapping("/registro-datos")
    public String registrarUsuario(
            @Valid @ModelAttribute("empleadoRegistroDTO") EmpleadoRegistroDTO empleadoRegistroDTO,
            BindingResult result,
            HttpSession session) {

        // Si hay errores de validación, se retorna a la misma vista
        if (result.hasErrors()) {
            return "empleadoRegistro";
        }

        session.setAttribute("empleadoRegistroDTO", empleadoRegistroDTO);

        // Redirige al formulario de registro de la dirección del empleado
        return "redirect:/empleados/registro-direccion";
    }






    @GetMapping("/registro-direccion")
    public String mostrarFormularioRegistroDireccion(@ModelAttribute EmpleadoRegistroDTO empleadoRegistroDTO,
                                                     HttpSession session) {

        session.setAttribute("empleadoRegistroDTO", empleadoRegistroDTO);

        return "empleadoDireccionRegistro"; // corresponde a empleadoDireccionRegistro.html
    }

    @PostMapping("/registro-direccion")
    public String RegistroDireccion(
            @Valid @ModelAttribute("empleadoRegistroDTO") EmpleadoRegistroDTO empleadoRegistroDTO,
            BindingResult result,
            HttpSession session,
            Model model) {

        // Si hay errores de validación, se retorna a la misma vista
        if (result.hasErrors()) {
            return "empleadoDireccionRegistro";
        }

        session.setAttribute("empleadoRegistroDTO", empleadoRegistroDTO);

        return "redirect:/empleados/registro-financiero"; // corresponde a registro.html
    }





    @GetMapping("/registro-financiero")
    public String mostrarFormularioRegistroFinanciero(@ModelAttribute EmpleadoRegistroDTO empleadoRegistroDTO,
                                                      HttpSession session) {

        session.setAttribute("empleadoRegistroDTO", empleadoRegistroDTO);

        return "empleadoDatosFinancieros"; // corresponde a empleadoDatosFinancieros.html
    }

    @PostMapping("/registro-financiero")
    public String RegistroFinanciero(
            @Valid @ModelAttribute("empleadoRegistroDTO") EmpleadoRegistroDTO empleadoRegistroDTO,
            BindingResult result,
            HttpSession session,
            Model model) {

        // Si hay errores de validación, se retorna a la misma vista
        if (result.hasErrors()) {
            return "empleadoDatosFinancieros";
        }

        session.setAttribute("empleadoRegistroDTO", empleadoRegistroDTO);



        return "redirect:/empleados/registro-finales"; // corresponde a registro.html
    }





    //    End-Point ===========> /datos-finales
    @GetMapping("/registro-finales")
    public String datosFinalesGet(@ModelAttribute EmpleadoRegistroDTO empleadoRegistroDTO,
                                  HttpSession session,
                                  Model model) {

        empleadoRegistroDTO = (EmpleadoRegistroDTO) session.getAttribute("empleadoRegistroDTO");
        model.addAttribute("datos", empleadoRegistroDTO);
        return "empleadoDatosFinales";
    }

    @PostMapping("/registro-finales")
    public String datosFinalesPost(@ModelAttribute EmpleadoRegistroDTO empleadoRegistroDTO,
                                   RedirectAttributes redirectAttrs,
                                   HttpSession session,
                                   Model model) {

        empleadoRegistroDTO = (EmpleadoRegistroDTO) session.getAttribute("empleadoRegistroDTO");
        // Mensaje que aparece en la ventana de alerta tras guardar datos
        redirectAttrs.addFlashAttribute("mensaje", "Datos guardados en Base de Datos");

        
        try {
            empleadoService.registrarEmpleado(empleadoRegistroDTO);
        } catch (RuntimeException ex) {
            // Si ocurre un error (por ejemplo, usuario ya existente), se añade al modelo y se vuelve al formulario
            model.addAttribute("error", ex.getMessage());
            return "empleadoDatosFinales";
        }
//        return "empleadoDatosFinales";
        return "redirect:/empleados/registro-finales"; // redirige a la vista resumen
    }


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
