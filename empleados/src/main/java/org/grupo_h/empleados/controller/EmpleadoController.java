package org.grupo_h.empleados.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.grupo_h.comun.entity.auxiliar.*;
import org.grupo_h.comun.repository.*;
import org.grupo_h.empleados.Validaciones.GruposValidaciones.*;
import org.grupo_h.comun.repository.GeneroRepository;
import org.grupo_h.comun.repository.PaisRepository;
import org.grupo_h.empleados.dto.EmpleadoDetalleDTO;
import org.grupo_h.empleados.dto.EmpleadoRegistroDTO;
import org.grupo_h.comun.repository.EmpleadoRepository;
import org.grupo_h.empleados.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;

/**
 * Controlador para gestionar las operaciones relacionadas con los empleados.
 */
@Controller
@RequestMapping("empleados")
public class EmpleadoController {

//Repositorios
    @Autowired
    private final EmpleadoRepository empleadoRepository;
    @Autowired
    private final GeneroRepository generoRepository;
    @Autowired
    private final PaisRepository paisRepository;
    @Autowired
    private final TipoDocumentoRepository tipoDocumentoRepository;
    @Autowired
    private final TipoViaRepository tipoViaRepository;
//Servicios
    @Autowired
    private final EmpleadoService empleadoService;
    @Autowired
    private final GeneroService generoService;
    @Autowired
    private final DepartamentoService departamentoService;
    @Autowired
    private final EspecialidadesEmpleadoService especialidadesEmpleadoService;

    public EmpleadoController(EmpleadoService empleadoService, EmpleadoRepository empleadoRepository, GeneroRepository generoRepository, PaisRepository paisRepository, TipoDocumentoRepository tipoDocumentoRepository, DepartamentoRepository departamentoRepository, TipoViaRepository tipoViaRepository, GeneroService generoService, DepartamentoService departamentoService, EspecialidadesEmpleadoService especialidadesEmpleadoService) {
        this.empleadoService = empleadoService;
        this.empleadoRepository = empleadoRepository;
        this.generoRepository = generoRepository;
        this.paisRepository = paisRepository;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
        this.tipoViaRepository = tipoViaRepository;
        this.generoService = generoService;
        this.departamentoService = departamentoService;
        this.especialidadesEmpleadoService = especialidadesEmpleadoService;
    }

    /**
     * Crea o recupera un objeto EmpleadoRegistroDTO de la sesión.
     *
     * @param session La sesión HTTP actual.
     * @return El objeto EmpleadoRegistroDTO.
     */
    @ModelAttribute("empleadoRegistroDTO")
    private EmpleadoRegistroDTO getEmpleadoDTO(HttpSession session) {
        EmpleadoRegistroDTO empleadoRegistroDTO = (EmpleadoRegistroDTO) session.getAttribute("empleadoRegistroDTO");
        if (empleadoRegistroDTO == null) {
            empleadoRegistroDTO = new EmpleadoRegistroDTO();
            session.setAttribute("empleadoRegistroDTO", empleadoRegistroDTO);
        }
        return empleadoRegistroDTO;
    }








    /**
     * Muestra el formulario de registro de datos básicos del empleado.
     *
     * @param empleadoRegistroDTO El DTO de registro del empleado.
     * @param session             La sesión HTTP actual.
     * @return La vista del formulario de registro.
     */
    @GetMapping("/registro-datos")
    public String mostrarFormularioRegistro(@ModelAttribute EmpleadoRegistroDTO empleadoRegistroDTO,
                                            HttpSession session,
                                            Model model) {

        // Obtener la lista de géneros usando el servicio
        List<Genero> generos = generoService.obtenerGeneros();

        // Asignar el primer género en el formulario si generoSeleccionado es nulo
        generoService.asignarPrimerGeneroSiEsNulo(empleadoRegistroDTO, generos);


        session.setAttribute("empleadoRegistroDTO", empleadoRegistroDTO);
        model.addAttribute("generos", generos);
        model.addAttribute("paises", paisRepository.findAll());
        model.addAttribute("listaPaises", paisRepository.findAll());
        return "empleadoRegistro";
    }

    /**
     * Procesa el formulario de registro de datos básicos del empleado.
     *
     * @param empleadoRegistroDTO El DTO de registro del empleado.
     * @param result              El resultado de la validación.
     * @param session             La sesión HTTP actual.
     * @return Redirección al siguiente paso del registro.
     */
    @PostMapping("/registro-datos")
    public String registrarUsuario(
            @Validated(DatosPersonales.class) @ModelAttribute("empleadoRegistroDTO") EmpleadoRegistroDTO empleadoRegistroDTO,
            BindingResult result,
            HttpSession session,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("generos", generoService.obtenerGeneros());
            model.addAttribute("paises", paisRepository.findAll());
            // Imprimir los errores para depurar
            result.getAllErrors().forEach(error -> System.out.println(error.toString()));
            return "empleadoRegistro";
        }
        System.out.println("Yendo al redirect");
        session.setAttribute("empleadoRegistroDTO", empleadoRegistroDTO);
        System.out.println(empleadoRegistroDTO);
        return "redirect:/empleados/registro-direccion";
    }














    /**
     * Muestra el formulario de registro de la dirección del empleado.
     *
     * @param empleadoRegistroDTO El DTO de registro del empleado.
     * @param session             La sesión HTTP actual.
     * @return La vista del formulario de registro de dirección.
     */
    @GetMapping("/registro-direccion")
    public String mostrarFormularioRegistroDireccion(@ModelAttribute EmpleadoRegistroDTO empleadoRegistroDTO,
                                                     HttpSession session,
                                                     Model model) {
        session.setAttribute("empleadoRegistroDTO", empleadoRegistroDTO);

        model.addAttribute("tiposDocumento",tipoDocumentoRepository.findAll());
        model.addAttribute("tiposVia",tipoViaRepository.findAll());
        model.addAttribute("paises", paisRepository.findAll());
        return "empleadoDireccionRegistro";
    }

    /**
     * Procesa el formulario de registro de la dirección del empleado.
     *
     * @param empleadoRegistroDTO El DTO de registro del empleado.
     * @param result              El resultado de la validación.
     * @param session             La sesión HTTP actual.
     * @param model               El modelo para pasar datos a la vista.
     * @return Redirección al siguiente paso del registro.
     */
    @PostMapping("/registro-direccion")
    public String RegistroDireccion(
            @Validated(DatosRegistroDireccion.class) @ModelAttribute("empleadoRegistroDTO") EmpleadoRegistroDTO empleadoRegistroDTO,
            BindingResult result,
            HttpSession session,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("tiposDocumento",tipoDocumentoRepository.findAll());
            model.addAttribute("tiposVia",tipoViaRepository.findAll());
            model.addAttribute("paises", paisRepository.findAll());
            // Imprimir los errores para depurar
            result.getAllErrors().forEach(error -> System.out.println(error.toString()));
            return "empleadoDireccionRegistro";
        }
        session.setAttribute("empleadoRegistroDTO", empleadoRegistroDTO);
        return "redirect:/empleados/registro-departamento";
    }







    @GetMapping("/registro-departamento")
    public String mostrarFormularioRegistroDepartamento(@ModelAttribute EmpleadoRegistroDTO empleadoRegistroDTO,
                                                        HttpSession session,
                                                        Model model) {
        session.setAttribute("empleadoRegistroDTO", empleadoRegistroDTO);

        model.addAttribute("departamentos",departamentoService.findAll());
        model.addAttribute("especialidades",especialidadesEmpleadoService.findAll());
        System.out.println("Hola");
        return "datosDepartamento";
    }

    @PostMapping("/registro-departamento")
    public String registrarDepartamento(
            @Validated(DatosDepartamento.class) @ModelAttribute("empleadoRegistroDTO") EmpleadoRegistroDTO empleadoRegistroDTO,
            BindingResult result,
            HttpSession session,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("departamentos",departamentoService.findAll());
            model.addAttribute("especialidades",especialidadesEmpleadoService.findAll());
            // Imprimir los errores para depurar
            result.getAllErrors().forEach(error -> System.out.println(error.toString()));
            System.out.println("Estoy en errores departamento");
            return "datosDepartamento";
        }
        System.out.println("Me voy al siguiente registro");
        session.setAttribute("empleadoRegistroDTO", empleadoRegistroDTO);
        System.out.println(empleadoRegistroDTO);
        return "redirect:/empleados/registro-financiero";
    }











    /**
     * Muestra el formulario de registro de datos financieros del empleado.
     *
     * @param empleadoRegistroDTO El DTO de registro del empleado.
     * @param session             La sesión HTTP actual.
     * @return La vista del formulario de registro de datos financieros.
     */
    @GetMapping("/registro-financiero")
    public String mostrarFormularioRegistroFinanciero(@ModelAttribute EmpleadoRegistroDTO empleadoRegistroDTO, HttpSession session) {
        session.setAttribute("empleadoRegistroDTO", empleadoRegistroDTO);
        return "empleadoDatosFinancieros";
    }

    /**
     * Procesa el formulario de registro de datos financieros del empleado.
     *
     * @param empleadoRegistroDTO El DTO de registro del empleado.
     * @param result              El resultado de la validación.
     * @param archivoAdjunto      El archivo adjunto subido.
     * @param session             La sesión HTTP actual.
     * @param model               El modelo para pasar datos a la vista.
     * @return Redirección al siguiente paso del registro.
     */
    @PostMapping("/registro-financiero")
    public String RegistroFinanciero(
            @Valid @ModelAttribute("empleadoRegistroDTO") EmpleadoRegistroDTO empleadoRegistroDTO,
            BindingResult result,
            HttpSession session,
            Model model) {
        EmpleadoRegistroDTO dtoSesion = getEmpleadoDTO(session);

        if (empleadoRegistroDTO.getCuentaCorrienteDTO() != null) {
            dtoSesion.setCuentaCorrienteDTO(empleadoRegistroDTO.getCuentaCorrienteDTO());
        }

//        if (archivoAdjunto != null && !archivoAdjunto.isEmpty()) {
//            try {
//                dtoSesion.setFotografia(archivoAdjunto.getBytes());
//                dtoSesion.setArchivoNombreOriginal(archivoAdjunto.getOriginalFilename());
//            } catch (IOException e) {
//                model.addAttribute("errorArchivo", "Error al procesar el archivo subido.");
//                model.addAttribute("empleadoRegistroDTO", dtoSesion);
//                return "empleadoDatosFinancieros";
//            }
//        } else {
//            dtoSesion.setFotografia(null);
//            dtoSesion.setArchivoNombreOriginal(null);
//        }

        if (result.hasErrors()) {
            return "empleadoDatosFinancieros";
        }

        session.setAttribute("empleadoRegistroDTO", dtoSesion);
        return "redirect:/empleados/registro-finales";
    }









    /**
     * Muestra el resumen final de los datos del empleado antes de guardar.
     *
     * @param empleadoRegistroDTO El DTO de registro del empleado.
     * @param session             La sesión HTTP actual.
     * @param model               El modelo para pasar datos a la vista.
     * @return La vista del resumen final.
     */
    @GetMapping("/registro-finales")
    public String datosFinalesGet(@ModelAttribute EmpleadoRegistroDTO empleadoRegistroDTO, HttpSession session, Model model) {
        empleadoRegistroDTO = (EmpleadoRegistroDTO) session.getAttribute("empleadoRegistroDTO");
        model.addAttribute("datos", empleadoRegistroDTO);
        if (empleadoRegistroDTO.getArchivoNombreOriginal() != null && !empleadoRegistroDTO.getArchivoNombreOriginal().isEmpty()) {
            model.addAttribute("nombreArchivo", empleadoRegistroDTO.getArchivoNombreOriginal());
        }
        return "empleadoDatosFinales";
    }

    /**
     * Procesa el guardado final de los datos del empleado.
     *
     * @param empleadoRegistroDTO El DTO de registro del empleado.
     * @param redirectAttrs       Atributos para redirección.
     * @param session             La sesión HTTP actual.
     * @param model               El modelo para pasar datos a la vista.
     * @return Redirección a la vista de resumen o al formulario en caso de error.
     */
    @PostMapping("/registro-finales")
    public String datosFinalesPost(@ModelAttribute EmpleadoRegistroDTO empleadoRegistroDTO, RedirectAttributes redirectAttrs, HttpSession session, Model model) {
        empleadoRegistroDTO = (EmpleadoRegistroDTO) session.getAttribute("empleadoRegistroDTO");
        // Mensaje que aparece en la ventana de alerta tras guardar datos
        redirectAttrs.addFlashAttribute("mensaje", "Datos guardados en Base de Datos");

        try {
            empleadoService.registrarEmpleado(empleadoRegistroDTO);
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            return "empleadoDatosFinales";
        }
        return "redirect:/empleados/registro-finales";
    }















    /**
     * Muestra los detalles de un empleado específico.
     *
     * @param model   El modelo para pasar datos a la vista.
     * @param session La sesion del navegador
     * @return La vista de detalles del empleado.
     */
    // TODO: Implementar la restriccion de acceso a aqui sin iniciar sesion con el usuario
    @GetMapping("/detalle")
    public String obtenerDetalleEmpleado(Model model, HttpSession session) {
//        EmpleadoDetalleDTO empDTO = (EmpleadoDetalleDTO) session.getAttribute("empleadoDetalleDTO");
//        UUID id = empDTO.getId();
//        Optional<EmpleadoDetalleDTO> empleadoOpt = empleadoService.obtenerDetalleEmpleado(id);
//        if (empleadoOpt.isPresent()) {
//            model.addAttribute("empleado", empleadoOpt.get());
//            return "detalleEmpleado";
//        } else {
//            return "detalleEmpleado";
//        }
        return "";
    }
}
