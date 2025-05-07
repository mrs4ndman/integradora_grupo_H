package org.grupo_h.empleados.controller;



import jakarta.servlet.http.HttpSession;
import org.grupo_h.comun.entity.auxiliar.Genero;
import org.grupo_h.comun.repository.*;
import org.grupo_h.empleados.Validaciones.GruposValidaciones.*;
import org.grupo_h.empleados.dto.*;
import org.grupo_h.empleados.service.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private final TipoTarjetaCreditoRepository tipoTarjetaCreditoRepository;
    @Autowired
    private final EntidadBancariaRepository entidadBancariaRepository;

    private final ModelMapper modelMapper;
//Servicios
    @Autowired
    private final EmpleadoService empleadoService;
    @Autowired
    private final GeneroService generoService;
    @Autowired
    private final DepartamentoService departamentoService;
    @Autowired
    private final EspecialidadesEmpleadoService especialidadesEmpleadoService;
    @Autowired
    private final EntidadBancariaService entidadBancariaService;
    @Autowired
    private final TipoTarjetaService tipoTarjetaService;
    @Autowired
    private TipoViaService tipoViaService;
    @Autowired
    private TipoDocumentoService tipoDocumentoService;

    public EmpleadoController(EmpleadoService empleadoService, EmpleadoRepository empleadoRepository, GeneroRepository generoRepository, PaisRepository paisRepository, TipoDocumentoRepository tipoDocumentoRepository, DepartamentoRepository departamentoRepository, TipoViaRepository tipoViaRepository, TipoTarjetaCreditoRepository tipoTarjetaCreditoRepository, EntidadBancariaRepository entidadBancariaRepository, ModelMapper modelMapper, GeneroService generoService, DepartamentoService departamentoService, EspecialidadesEmpleadoService especialidadesEmpleadoService, EntidadBancariaService entidadBancariaService, TipoTarjetaService tipoTarjetaService) {
        this.empleadoService = empleadoService;
        this.empleadoRepository = empleadoRepository;
        this.generoRepository = generoRepository;
        this.paisRepository = paisRepository;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
        this.tipoViaRepository = tipoViaRepository;
        this.tipoTarjetaCreditoRepository = tipoTarjetaCreditoRepository;
        this.entidadBancariaRepository = entidadBancariaRepository;
        this.modelMapper = modelMapper;
        this.generoService = generoService;
        this.departamentoService = departamentoService;
        this.especialidadesEmpleadoService = especialidadesEmpleadoService;
        this.entidadBancariaService = entidadBancariaService;
        this.tipoTarjetaService = tipoTarjetaService;
    }

    /**
     * Crea o recupera un objeto EmpleadoRegistroDTO de la sesión.
     *
     * @param session La sesión HTTP actual.
     * @return El objeto EmpleadoRegistroDTO.
     */
    @ModelAttribute("empleadoRegistroDTO")
    private EmpleadoRegistroDTO getEmpleadoRegistroDTO(HttpSession session) {
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

//        // Obtener la lista de géneros usando el servicio
//        List<Genero> gen = generoService.obtenerGeneros();

            List<Genero> generos = generoService.obtenerGeneros();
            if (empleadoRegistroDTO.getGeneroSeleccionadoDTO() == null && !generos.isEmpty()) {
                empleadoRegistroDTO.getGeneroSeleccionadoDTO(); // Set first as default
            }


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


            List<TipoViaDTO> tipoViaDTO = tipoViaService.obtenertodasTipoVia().stream()
                    .map(e -> modelMapper.map(e, TipoViaDTO.class))
                    .collect(Collectors.toList());
            model.addAttribute("tiposVia", tipoViaDTO);

            List<TipoDocumentoDTO> tipoDocumentoDTO = tipoDocumentoService.obtenertodosTiposDocumento().stream()
                    .map(e -> modelMapper.map(e, TipoDocumentoDTO.class))
                    .collect(Collectors.toList());
            model.addAttribute("tiposDocumento",tipoDocumentoDTO);

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
                List<TipoViaDTO> tipoViaDTO = tipoViaService.obtenertodasTipoVia().stream()
                        .map(e -> modelMapper.map(e, TipoViaDTO.class))
                        .collect(Collectors.toList());
                model.addAttribute("tiposVia",tipoViaDTO);

                List<TipoDocumentoDTO> tipoDocumentoDTO = tipoDocumentoService.obtenertodosTiposDocumento().stream()
                        .map(e -> modelMapper.map(e, TipoDocumentoDTO.class))
                        .collect(Collectors.toList());
                model.addAttribute("tiposDocumento",tipoDocumentoDTO);

                model.addAttribute("paises", paisRepository.findAll());
                // Imprimir los errores para depurar
                result.getAllErrors().forEach(error -> System.out.println(error.toString()));
                System.out.println(empleadoRegistroDTO);
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

            // Cargar las especialidades y los departamentos
            cargarEspecialidades(empleadoRegistroDTO, model);
//            System.out.println(empleadoRegistroDTO);

            return "datosDepartamento";
        }

        @PostMapping("/registro-departamento")
        public String registrarDepartamento(
                @Validated(DatosDepartamento.class) @ModelAttribute("empleadoRegistroDTO") EmpleadoRegistroDTO empleadoRegistroDTO,
                BindingResult result,
                HttpSession session,
                Model model) {

            if (result.hasErrors()) {
                // Cargar las especialidades y los departamentos
                cargarEspecialidades(empleadoRegistroDTO, model);
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
        public String mostrarFormularioRegistroFinanciero(@ModelAttribute EmpleadoRegistroDTO empleadoRegistroDTO,
                HttpSession session,
                Model model) {
            session.setAttribute("empleadoRegistroDTO", empleadoRegistroDTO);

            model.addAttribute("entidades", entidadBancariaRepository.findAll());
            model.addAttribute("tiposTarjeta", tipoTarjetaService.listarTipoTarjetaCredito());
            return "empleadoDatosFinancieros";
        }

        /**
         * Procesa el formulario de registro de datos financieros del empleado.
         *
         * @param empleadoRegistroDTO El DTO de registro del empleado.
         * @param result              El resultado de la validación.
         * @param session             La sesión HTTP actual.
         * @param model               El modelo para pasar datos a la vista.
         * @return Redirección al siguiente paso del registro.
         */
        @PostMapping("/registro-financiero")
        public String RegistroFinanciero(
                @Validated(DatosFinancieros.class) @ModelAttribute("empleadoRegistroDTO") EmpleadoRegistroDTO empleadoRegistroDTO,
                BindingResult result,
                HttpSession session,
                Model model) {
            // ModelAttribute instanciado arriba
            EmpleadoRegistroDTO dtoSesion = getEmpleadoRegistroDTO(session);

            List<EntidadBancariaDTO> entidadesDTO = entidadBancariaService.listarEntidadBancaria().stream()
                    .map(e -> modelMapper.map(e, EntidadBancariaDTO.class))
                    .collect(Collectors.toList());
            model.addAttribute("entidades", entidadesDTO);

            List<TipoTarjetaCreditoDTO> tipoTarjetaCreditoDTOS = tipoTarjetaCreditoRepository.findAll().stream()
                    .map(e -> modelMapper.map(e, TipoTarjetaCreditoDTO.class))
                    .collect(Collectors.toList());
            model.addAttribute("tiposTarjeta", tipoTarjetaCreditoDTOS);



            if (result.hasErrors()) {
                // Obtener entidades bancarias desde la base de datos
                model.addAttribute("entidades",entidadBancariaService.listarEntidadBancaria());
                model.addAttribute("tiposTarjeta", tipoTarjetaCreditoRepository.findAll());
                // Imprimir los errores para depurar
                result.getAllErrors().forEach(error -> System.out.println(error.toString()));

                session.setAttribute("empleadoRegistroDTO", dtoSesion);
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


            if (empleadoRegistroDTO == null) {
                empleadoRegistroDTO = new EmpleadoRegistroDTO();
            }

            // Se verifica los nulls para que muestre la pantalla resumen (último Paso)
            if (empleadoRegistroDTO.getDireccionDTO() == null) {
                empleadoRegistroDTO.setDireccionDTO(new DireccionDTO());
            }

            if(empleadoRegistroDTO.getCuentaCorrienteDTO() == null){
                empleadoRegistroDTO.setCuentaCorrienteDTO(new CuentaCorrienteDTO());
            }

            if (empleadoRegistroDTO.getTarjetasCreditoDTO() == null){
                empleadoRegistroDTO.setTarjetasCreditoDTO(new TarjetaCreditoDTO());
            }


            model.addAttribute("datos", empleadoRegistroDTO);

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
        public String datosFinalesPost(@Validated(DatosFinales.class) @ModelAttribute EmpleadoRegistroDTO empleadoRegistroDTO,
                                       BindingResult result,
                                       RedirectAttributes redirectAttrs,
                                       HttpSession session,
                                       Model model) {
            EmpleadoRegistroDTO dtoSesion = (EmpleadoRegistroDTO) session.getAttribute("empleadoRegistroDTO");

            if (result.hasErrors()) {
                // Imprimir los errores para depurar
                result.getAllErrors().forEach(error -> System.out.println(error.toString()));
                return "empleadoDatosFinales";
            }

            // Mensaje que aparece en la ventana de alerta tras guardar datos
            redirectAttrs.addFlashAttribute("mensaje", "Datos guardados en Base de Datos");

            try {
                empleadoService.registrarEmpleado(dtoSesion)  ;
            } catch (RuntimeException ex) {
                model.addAttribute("error", ex.getMessage());
                return "empleadoDatosFinales";
            } catch (Exception e) {
                throw new RuntimeException(e);
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

    private void cargarEspecialidades(@ModelAttribute("empleadoRegistroDTO") @Validated(DatosDepartamento.class) EmpleadoRegistroDTO empleadoRegistroDTO, Model model) {
        if (empleadoRegistroDTO.getEspecialidadesSeleccionadasDTO() == null || empleadoRegistroDTO.getEspecialidadesSeleccionadasDTO().isEmpty()) {
            List<EspecialidadesEmpleadoDTO> especialidades = especialidadesEmpleadoService.obtenerTodasEspecialidadesEmpleadoDTO();
//            especialidades.forEach(e -> {
//                if (e.getNombreEspecialidad() == null) {
//                }
//            });

            empleadoRegistroDTO.setEspecialidadesSeleccionadasDTO(especialidades);
        }
        List<DepartamentoDTO> departamentosDTO = departamentoService.obtenerTodosDepartamentos().stream()
                .map(e -> modelMapper.map(e, DepartamentoDTO.class))
                .collect(Collectors.toList());
        model.addAttribute("departamentos", departamentosDTO);
//        model.addAttribute("departamentos",departamentoService.obtenerTodosDepartamentos());
    }

}









