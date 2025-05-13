package org.grupo_h.empleados.controller;



import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.comun.entity.Etiqueta;
import org.grupo_h.comun.entity.Usuario;
import org.grupo_h.comun.entity.auxiliar.Genero;
import org.grupo_h.comun.repository.*;
import org.grupo_h.comun.service.DepartamentoService;
import org.grupo_h.empleados.Validaciones.GruposValidaciones.*;
import org.grupo_h.empleados.dto.*;
import org.grupo_h.empleados.service.*;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador para gestionar las operaciones relacionadas con los empleados.
 */
@Slf4j
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
    @Autowired
    private final UsuarioRepository usuarioRepository;
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
    private final UsuarioService usuarioService;
    @Autowired
    private final EtiquetaService etiquetaService;
    @Autowired
    private final ModelMapper modelMapper;
    @Autowired
    private final TipoViaService tipoViaService;
    @Autowired
    private final TipoDocumentoService tipoDocumentoService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public EmpleadoController(EmpleadoService empleadoService, EmpleadoRepository empleadoRepository, GeneroRepository generoRepository, PaisRepository paisRepository, TipoDocumentoRepository tipoDocumentoRepository, DepartamentoRepository departamentoRepository, TipoViaRepository tipoViaRepository, TipoTarjetaCreditoRepository tipoTarjetaCreditoRepository, TipoTarjetaCreditoRepository tipoTarjetaCreditoRepository1, EntidadBancariaRepository entidadBancariaRepository, UsuarioRepository usuarioRepository, GeneroService generoService, DepartamentoService departamentoService, EspecialidadesEmpleadoService especialidadesEmpleadoService, EntidadBancariaService entidadBancariaService, TipoTarjetaService tipoTarjetaService, UsuarioService usuarioService, ModelMapper modelMapper, EtiquetaService etiquetaService, TipoViaService tipoViaService, TipoDocumentoService tipoDocumentoService) {
        this.empleadoService = empleadoService;
        this.empleadoRepository = empleadoRepository;
        this.generoRepository = generoRepository;
        this.paisRepository = paisRepository;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
        this.tipoViaRepository = tipoViaRepository;
        this.tipoTarjetaCreditoRepository = tipoTarjetaCreditoRepository1;
        this.entidadBancariaRepository = entidadBancariaRepository;
        this.usuarioRepository = usuarioRepository;
        this.generoService = generoService;
        this.departamentoService = departamentoService;
        this.especialidadesEmpleadoService = especialidadesEmpleadoService;
        this.entidadBancariaService = entidadBancariaService;
        this.tipoTarjetaService = tipoTarjetaService;
        this.usuarioService = usuarioService;
        this.etiquetaService = etiquetaService;
        this.modelMapper = modelMapper;
        this.tipoViaService = tipoViaService;
        this.tipoDocumentoService = tipoDocumentoService;
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
            String emailUsuario = (String) session.getAttribute("emailAutenticado");
            Optional<Usuario> usuario = usuarioService.findByEmail(emailUsuario);
            if (usuario.isPresent()) {
                empleadoRegistroDTO.setUsuarioId(usuario.get().getId());
            } else {
                logger.error("No se pudo obtener el ID del usuario autenticado.");
                empleadoRegistroDTO.setUsuarioId(null);
            }
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
        if (session.getAttribute("emailAutenticado") == null) {
            logger.info("El usuario no ha iniciado sesión. Redirigiendo a la página de inicio de sesión.");
            return "redirect:/usuarios/inicio-sesion";
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
        model.addAttribute("tiposDocumento", tipoDocumentoDTO);

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
            model.addAttribute("tiposVia", tipoViaDTO);

            List<TipoDocumentoDTO> tipoDocumentoDTO = tipoDocumentoService.obtenertodosTiposDocumento().stream()
                    .map(e -> modelMapper.map(e, TipoDocumentoDTO.class))
                    .collect(Collectors.toList());
            model.addAttribute("tiposDocumento", tipoDocumentoDTO);

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
            model.addAttribute("entidades", entidadBancariaService.listarEntidadBancaria());
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

        // Se verifica los nulls para que muestre la pantalla resumen (último Paso)
        if (empleadoRegistroDTO == null) {
            empleadoRegistroDTO = new EmpleadoRegistroDTO();
        }

        if(empleadoRegistroDTO.getGeneroSeleccionadoDTO() == null){
            empleadoRegistroDTO.setGeneroSeleccionadoDTO(new GeneroDTO());
        }

        if(empleadoRegistroDTO.getTipoDocumentoDTO() == null){
            empleadoRegistroDTO.setTipoDocumentoDTO(new TipoDocumentoDTO());
        }


        if (empleadoRegistroDTO.getDireccionDTO() == null) {
            empleadoRegistroDTO.setDireccionDTO(new DireccionDTO());
        }

        if(empleadoRegistroDTO.getDireccionDTO().getTipoViaDireccionPpalDTO() == null){
            empleadoRegistroDTO.getDireccionDTO().setTipoViaDireccionPpalDTO(new TipoViaDTO());
        }

        if (empleadoRegistroDTO.getCuentaCorrienteDTO() == null) {
            empleadoRegistroDTO.setCuentaCorrienteDTO(new CuentaCorrienteDTO());
        }

        if (empleadoRegistroDTO.getTarjetasCreditoDTO() == null) {
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

        String emailUsuario = (String) session.getAttribute("emailAutenticado");
        Optional<Usuario> usuario = usuarioService.findByEmail(emailUsuario);
        if (usuario.isPresent()) {
            logger.info("Se ha encontrado el ID del usuario autenticado.");
        } else {
            logger.error("No se pudo obtener el ID del usuario autenticado. Hola");
            return "redirect:/usuarios/inicio-sesion";
        }

        try {
            empleadoService.registrarEmpleado(dtoSesion, usuario.get().getId());
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            // Si el usuario vuelve a introducir los datos, se redirige al área personal
            return "redirect:/areaPersonal";
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
    }
//        model.addAttribute("departamentos",departamentoService.obtenerTodosDepartamentos());

    // --- NUEVOS Y MODIFICADOS ENDPOINTS PARA ETIQUETADO ---

    /**
     * NUEVO: Muestra la página dedicada para gestionar etiquetas de un subordinado específico.
     * Requiere que el usuario autenticado sea el jefe directo.
     */
    @GetMapping("/{subordinadoId}/gestionar-etiquetas")
    @PreAuthorize("@empleadoServiceImpl.esJefeDirecto(#subordinadoId)")
    public String mostrarGestionarEtiquetas(@PathVariable UUID subordinadoId, Model model) {
        try {
            // Cargar datos del subordinado (necesitamos al menos nombre y ID, y sus etiquetas actuales)
            // Usaremos EmpleadoDetalleDTO ya que probablemente contiene las etiquetas. Ajusta si usas otro DTO.
            Optional<EmpleadoDetalleDTO> empleadoOpt = empleadoService.findEmpleadoDetalleById(subordinadoId);
            if (empleadoOpt.isEmpty()) {
                throw new EntityNotFoundException("Empleado no encontrado con ID: " + subordinadoId);
            }
            EmpleadoDetalleDTO empleado = empleadoOpt.get();
            model.addAttribute("empleado", empleado); // Para mostrar nombre y etiquetas actuales

            // Cargar todas las etiquetas disponibles para los formularios
            List<EtiquetaDTO> todasLasEtiquetas = etiquetaService.findAll()
                    .stream()
                    .map(et -> modelMapper.map(et, EtiquetaDTO.class))
                    .collect(Collectors.toList());
            model.addAttribute("todasLasEtiquetas", todasLasEtiquetas);

            return "gestionarEtiquetasEmpleado"; // Nombre de la nueva vista Thymeleaf

        } catch (EntityNotFoundException e) {
            // Considera añadir un mensaje flash y redirigir a una página de error o al área personal
            // redirectAttributes.addFlashAttribute("errorMessage", "Error: No se encontró el empleado.");
            return "redirect:/areaPersonal"; // O una página de error
        } catch (Exception e) {
            // redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado.");
            return "redirect:/areaPersonal";
        }
    }


    /**
     * Endpoint para ASIGNAR una etiqueta existente a un subordinado.
     * Redirige de vuelta a la página de gestión de etiquetas.
     */
    @PostMapping("/{subordinadoId}/etiquetas")
    @PreAuthorize("@empleadoServiceImpl.esJefeDirecto(#subordinadoId)")
    public String asignarEtiqueta(@PathVariable UUID subordinadoId,
                                  @RequestParam UUID etiquetaId,
                                  RedirectAttributes redirectAttributes) {
        String redirectTo = "redirect:/empleados/" + subordinadoId + "/gestionar-etiquetas"; // Nueva redirección
        try {
            empleadoService.asignarEtiquetaASubordinado(subordinadoId, etiquetaId);
            redirectAttributes.addFlashAttribute("successMessage", "Etiqueta asignada correctamente.");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: No se encontró el empleado o la etiqueta.");
        } catch (AccessDeniedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: No tienes permiso para realizar esta acción.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado al asignar la etiqueta.");
        }
        return redirectTo; // Vuelve a la página de gestión
    }

    /**
     * Endpoint para CREAR y ASIGNAR una NUEVA etiqueta a un subordinado.
     * Redirige de vuelta a la página de gestión de etiquetas.
     */
    @PostMapping("/{subordinadoId}/etiquetas/nueva")
    @PreAuthorize("@empleadoServiceImpl.esJefeDirecto(#subordinadoId)")
    public String asignarNuevaEtiqueta(@PathVariable UUID subordinadoId,
                                       @RequestParam String nombreNuevaEtiqueta,
                                       RedirectAttributes redirectAttributes) {
        String redirectTo = "redirect:/empleados/" + subordinadoId + "/gestionar-etiquetas"; // Nueva redirección
        if (nombreNuevaEtiqueta == null || nombreNuevaEtiqueta.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "El nombre de la nueva etiqueta no puede estar vacío.");
            return redirectTo;
        }
        try {
            Etiqueta etiqueta = etiquetaService.findOrCreateEtiqueta(nombreNuevaEtiqueta.trim());
            empleadoService.asignarEtiquetaASubordinado(subordinadoId, etiqueta.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Nueva etiqueta '" + etiqueta.getNombre() + "' creada y asignada.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: No se encontró el empleado.");
        } catch (AccessDeniedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: No tienes permiso para realizar esta acción.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado al asignar la nueva etiqueta.");
        }
        return redirectTo; // Vuelve a la página de gestión
    }


    /**
     * Endpoint para ELIMINAR una etiqueta de un subordinado.
     * Redirige de vuelta a la página de gestión de etiquetas.
     */
    @PostMapping("/{subordinadoId}/etiquetas/{etiquetaId}/eliminar")
    @PreAuthorize("@empleadoServiceImpl.esJefeDirecto(#subordinadoId)")
    public String eliminarEtiqueta(@PathVariable UUID subordinadoId,
                                   @PathVariable UUID etiquetaId,
                                   RedirectAttributes redirectAttributes) {
        String redirectTo = "redirect:/empleados/" + subordinadoId + "/gestionar-etiquetas"; // Nueva redirección
        try {
            empleadoService.eliminarEtiquetaDeSubordinado(subordinadoId, etiquetaId);
            redirectAttributes.addFlashAttribute("successMessage", "Etiqueta eliminada correctamente.");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: No se pudo eliminar la etiqueta (¿ya estaba eliminada?).");
        } catch (AccessDeniedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: No tienes permiso para realizar esta acción.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado al eliminar la etiqueta.");
        }
        return redirectTo; // Vuelve a la página de gestión
    }

    @GetMapping("/etiquetado-masivo")
    @PreAuthorize("isAuthenticated()")
    public String mostrarFormularioEtiquetadoMasivo(Model model) {
        try {
            // Obtener subordinados del jefe autenticado
            List<Empleado> subordinados = empleadoService.getSubordinadosDelJefeAutenticado();
            List<EmpleadoSimpleDTO> subordinadosDTO = subordinados.stream()
                    .map(e -> new EmpleadoSimpleDTO(e.getId(), e.getNombre(), e.getApellidos())) // Mapeo simple
                    .collect(Collectors.toList());
            model.addAttribute("subordinadosDisponibles", subordinadosDTO);

            // Obtener todas las etiquetas
            List<EtiquetaDTO> todasLasEtiquetas = etiquetaService.findAll()
                    .stream()
                    .map(et -> modelMapper.map(et, EtiquetaDTO.class))
                    .collect(Collectors.toList());
            model.addAttribute("etiquetasDisponibles", todasLasEtiquetas);

            // Añadir un objeto DTO vacío para el binding del formulario
            model.addAttribute("etiquetadoMasivoRequest", new EtiquetadoMasivoRequestDTO());

        } catch (AccessDeniedException e) {
            // Redirigir a una página de error o al área personal
            model.addAttribute("errorMessage", "No tienes permiso para acceder a esta función.");
            return "areaPersonal"; // O una vista de error
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error al cargar la página de etiquetado masivo.");
            // Considera redirigir o mostrar una página de error genérica
            return "areaPersonal"; // O una vista de error
        }

        return "etiquetadoMasivo"; // Nombre de tu nueva vista Thymeleaf
    }

    @PostMapping("/etiquetado-masivo")
    @PreAuthorize("isAuthenticated()")
    public String procesarEtiquetadoMasivo(@Valid @ModelAttribute("etiquetadoMasivoRequest") EtiquetadoMasivoRequestDTO requestDTO,
                                           BindingResult bindingResult,
                                           Model model,
                                           RedirectAttributes redirectAttributes) {
        // ... (código sin cambios, sigue redirigiendo a /empleados/etiquetado-masivo) ...
        if (bindingResult.hasErrors()) {
            // Recargar datos necesarios para volver a mostrar el formulario con errores
            try {
                List<Empleado> subordinados = empleadoService.getSubordinadosDelJefeAutenticado();
                List<EmpleadoSimpleDTO> subordinadosDTO = subordinados.stream()
                        .map(e -> new EmpleadoSimpleDTO(e.getId(), e.getNombre(), e.getApellidos()))
                        .collect(Collectors.toList());
                model.addAttribute("subordinadosDisponibles", subordinadosDTO);

                List<EtiquetaDTO> todasLasEtiquetas = etiquetaService.findAll()
                        .stream()
                        .map(et -> modelMapper.map(et, EtiquetaDTO.class))
                        .collect(Collectors.toList());
                model.addAttribute("etiquetasDisponibles", todasLasEtiquetas);
            } catch (Exception e) {
                model.addAttribute("errorMessage", "Error al recargar datos del formulario.");
            }
            return "etiquetadoMasivo"; // Vuelve a mostrar el formulario
        }

        try {
            empleadoService.asignarEtiquetasMasivo(requestDTO.getEmpleadoIds(), requestDTO.getEtiquetaIds());
            redirectAttributes.addFlashAttribute("successMessage", "Etiquetas asignadas masivamente con éxito.");
            return "redirect:/empleados/etiquetado-masivo"; // Redirige de nuevo al form (o donde quieras)
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: Algún empleado o etiqueta seleccionada no existe.");
        } catch (AccessDeniedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: No tienes permiso para etiquetar a alguno de los empleados seleccionados.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado durante el etiquetado masivo.");
        }

        // Si hubo excepción, redirige de vuelta al formulario para mostrar el mensaje de error
        return "redirect:/empleados/etiquetado-masivo";
    }


}







