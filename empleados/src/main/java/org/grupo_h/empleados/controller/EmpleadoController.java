package org.grupo_h.empleados.controller;


import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.grupo_h.comun.auxiliar.RestPage;
import org.grupo_h.comun.entity.*;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

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
    @Autowired
    private final EtiquetaRepository etiquetaRepository;
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
    private final ProductoService productoService;
    @Autowired
    private final TipoDocumentoService tipoDocumentoService;
    @Autowired
    private final RestTemplate restTemplate;
    @Autowired
    private final FotografiaService fotografiaService;


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public EmpleadoController(EmpleadoService empleadoService, EmpleadoRepository empleadoRepository, GeneroRepository generoRepository, PaisRepository paisRepository, TipoDocumentoRepository tipoDocumentoRepository, DepartamentoRepository departamentoRepository, TipoViaRepository tipoViaRepository, TipoTarjetaCreditoRepository tipoTarjetaCreditoRepository, TipoTarjetaCreditoRepository tipoTarjetaCreditoRepository1, EntidadBancariaRepository entidadBancariaRepository, UsuarioRepository usuarioRepository, EtiquetaRepository etiquetaRepository, GeneroService generoService, DepartamentoService departamentoService, EspecialidadesEmpleadoService especialidadesEmpleadoService, EntidadBancariaService entidadBancariaService, TipoTarjetaService tipoTarjetaService, UsuarioService usuarioService, ModelMapper modelMapper, EtiquetaService etiquetaService, TipoViaService tipoViaService, ProductoService productoService, TipoDocumentoService tipoDocumentoService, RestTemplate restTemplate, FotografiaService fotografiaService) {
        this.empleadoService = empleadoService;
        this.empleadoRepository = empleadoRepository;
        this.generoRepository = generoRepository;
        this.paisRepository = paisRepository;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
        this.tipoViaRepository = tipoViaRepository;
        this.tipoTarjetaCreditoRepository = tipoTarjetaCreditoRepository1;
        this.entidadBancariaRepository = entidadBancariaRepository;
        this.usuarioRepository = usuarioRepository;
        this.etiquetaRepository = etiquetaRepository;
        this.generoService = generoService;
        this.departamentoService = departamentoService;
        this.especialidadesEmpleadoService = especialidadesEmpleadoService;
        this.entidadBancariaService = entidadBancariaService;
        this.tipoTarjetaService = tipoTarjetaService;
        this.usuarioService = usuarioService;
        this.etiquetaService = etiquetaService;
        this.modelMapper = modelMapper;
        this.tipoViaService = tipoViaService;
        this.productoService = productoService;
        this.tipoDocumentoService = tipoDocumentoService;
        this.restTemplate = restTemplate;
        this.fotografiaService = fotografiaService;
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
            @RequestParam("fotografiaDTO") MultipartFile multipartFile,
            Model model) throws IOException {

        if (result.hasErrors()) {
            model.addAttribute("generos", generoService.obtenerGeneros());
            model.addAttribute("paises", paisRepository.findAll());
            // Imprimir los errores para depurar
            result.getAllErrors().forEach(error -> System.out.println(error.toString()));
            return "empleadoRegistro";
        }

        System.out.println(multipartFile.getOriginalFilename());
        if(!multipartFile.isEmpty()){
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            empleadoRegistroDTO.setRutaArchivo(fileName);
            // Convertimos de MultipartFile a bytes y lo seteamos para guardarlo
            empleadoRegistroDTO.setFotografiaArchivo(multipartFile.getBytes());
        }else {
            empleadoRegistroDTO.setFotografiaDTO(null);
        }



        System.err.println(empleadoRegistroDTO.getFotografiaDTO());
        System.out.println("Estoy en el Post de registro de Datos del Empleado");

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

        if (empleadoRegistroDTO.getGeneroSeleccionadoDTO() == null) {
            empleadoRegistroDTO.setGeneroSeleccionadoDTO(new GeneroDTO());
        }

        if (empleadoRegistroDTO.getPaisNacimiento() == null) {
            empleadoRegistroDTO.setPaisNacimiento(new PaisDTO());
        }

        if (empleadoRegistroDTO.getTipoDocumentoDTO() == null) {
            empleadoRegistroDTO.setTipoDocumentoDTO(new TipoDocumentoDTO());
        }

        if (empleadoRegistroDTO.getDireccionDTO() == null) {
            empleadoRegistroDTO.setDireccionDTO(new DireccionDTO());
        }

        if (empleadoRegistroDTO.getDireccionDTO().getTipoViaDireccionPpalDTO() == null) {
            empleadoRegistroDTO.getDireccionDTO().setTipoViaDireccionPpalDTO(new TipoViaDTO());
        }

        if (empleadoRegistroDTO.getCuentaCorrienteDTO() == null) {
            empleadoRegistroDTO.setCuentaCorrienteDTO(new CuentaCorrienteDTO());
        }

        if (empleadoRegistroDTO.getCuentaCorrienteDTO().getEntidadBancaria() == null) {
            empleadoRegistroDTO.getCuentaCorrienteDTO().setEntidadBancaria(new EntidadBancariaDTO());
        }

        if (empleadoRegistroDTO.getTarjetasCreditoDTO() == null) {
            empleadoRegistroDTO.setTarjetasCreditoDTO(new TarjetaCreditoDTO());
        }

        if (empleadoRegistroDTO.getTarjetasCreditoDTO().getTipoTarjetaCreditoDTO() == null) {
            empleadoRegistroDTO.getTarjetasCreditoDTO().setTipoTarjetaCreditoDTO(new TipoTarjetaCreditoDTO());
        }

        if (empleadoRegistroDTO.getDepartamentoDTO() == null) {
            empleadoRegistroDTO.setDepartamentoDTO(new DepartamentoDTO());
        }

        if (empleadoRegistroDTO.getEspecialidadesSeleccionadasDTO() == null) {
            empleadoRegistroDTO.setEspecialidadesSeleccionadasDTO(new ArrayList<>());
        }


        // Convertir byte[] a Base64 si existe imagen
        String fotoBase64 = null;
        if (empleadoRegistroDTO.getFotografiaArchivo() != null && empleadoRegistroDTO.getFotografiaArchivo().length > 0) {
            fotoBase64 = "data:image/jpeg;base64," +
                    Base64.getEncoder().encodeToString(empleadoRegistroDTO.getFotografiaArchivo());
        }

        model.addAttribute("fotoBase64", fotoBase64);
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

            model.addAttribute("exito", "Datos guardados correctamente");

//            model.addAttribute("mensaje", "¡Sus datos se guardaron correctamente!");
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            System.err.println("Error RunTime: " + ex.getMessage());
            model.addAttribute("error", "Ya existe un empleado con el mismo ID o DNI");
            return "redirect:/empleados/registro-finales";
            // Si el usuario vuelve a introducir los datos, se redirige al área personal
//            return "redirect:/empleados/registro-finales";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            System.err.println("Error Exception" + e.getMessage());
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
    @GetMapping("/detalle")
    public String obtenerDetalleEmpleado(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String emailUsuario = (String) session.getAttribute("emailAutenticado");
        Optional<Usuario> usuario = usuarioService.findByEmail(emailUsuario);
        if (usuario.isPresent()) {
            Optional<Empleado> detallado = empleadoRepository.findById(usuario.get().getId());
            if (detallado.isPresent()) {
                EmpleadoDetalleDTO empDTO = modelMapper.map(detallado.get(), EmpleadoDetalleDTO.class);
                if (empDTO != null) {
                    model.addAttribute("empleadoDTO", empDTO);
                    return "detalleEmpleado";
                } else {
                    redirectAttributes.addFlashAttribute("error",
                            "No hay un empleado cargado en la sesion. Inicia sesion con tu usuario");
                    return "redirect:/usuarios/inicio-sesion";
                }
            }
        }
        return "redirect:/usuarios/inicio-sesion";
    }

    private void cargarEspecialidades(
            @ModelAttribute("empleadoRegistroDTO") @Validated(DatosDepartamento.class)
            EmpleadoRegistroDTO empleadoRegistroDTO,
            Model model) {

        // 1. Obtén siempre todas las especialidades disponibles
        List<EspecialidadesEmpleadoDTO> todas =
                especialidadesEmpleadoService.obtenerTodasEspecialidadesEmpleadoDTO();

        // 2. Construye un mapa { id -> estabaSeleccionada } a partir de lo que viene del POST
        Map<UUID, Boolean> seleccionadasMap = Optional.ofNullable(empleadoRegistroDTO.getEspecialidadesSeleccionadasDTO())
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.toMap(
                        EspecialidadesEmpleadoDTO::getId,
                        EspecialidadesEmpleadoDTO::isSeleccionada
                ));

        // 3. Recorre la lista completa y ajusta el flag 'seleccionada' según tu mapa
        todas.forEach(e -> e.setSeleccionada(seleccionadasMap.getOrDefault(e.getId(), false)));

        // 4. Sustituye la lista del DTO por ésta, ya con los flags restaurados
        empleadoRegistroDTO.setEspecialidadesSeleccionadasDTO(todas);

        // 5. Igual que antes, carga los departamentos
        List<DepartamentoDTO> departamentosDTO = departamentoService
                .obtenerTodosDepartamentos().stream()
                .map(e -> modelMapper.map(e, DepartamentoDTO.class))
                .collect(Collectors.toList());

        Map<UUID, String> nombresDept = departamentosDTO.stream()
                .collect(Collectors.toMap(DepartamentoDTO::getId, DepartamentoDTO::getNombreDept));

        model.addAttribute("departamentos", departamentosDTO);
    }
    // --- ENDPOINTS PARA ETIQUETADO ---

    @GetMapping("/gestion-etiquetas")
    public String mostrarGestionEtiquetas(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            Empleado jefe = obtenerJefeAutenticado(session);
            popularModeloParaGestionEtiquetas(model, jefe);
            return "gestionEtiquetas";
        } catch (AccessDeniedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/usuarios/inicio-sesion";
        } catch (Exception e) {
            logger.error("Error al cargar la página de gestión de etiquetas", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado al cargar la página.");
            return "redirect:/usuarios/info";
        }
    }

    @PostMapping("/etiquetado/nuevo")
    public String procesarNuevoEtiquetado(@Valid @ModelAttribute("asignarEtiquetaRequestDTO") AsignarEtiquetaRequestDTO requestDTO,
                                          BindingResult bindingResult,
                                          HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        Empleado jefe;
        try {
            jefe = obtenerJefeAutenticado(session);
        } catch (AccessDeniedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/usuarios/inicio-sesion";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.asignarEtiquetaRequestDTO", bindingResult);
            redirectAttributes.addFlashAttribute("asignarEtiquetaRequestDTO", requestDTO);
            redirectAttributes.addFlashAttribute("errorMessage", "Errores de validación en el formulario de nuevo etiquetado.");

            popularModeloParaGestionEtiquetas(model, jefe);
            model.addAttribute("errorMessage", "Por favor, corrija los errores del formulario de nuevo etiquetado.");
            return "gestionEtiquetas";
        }

        try {
            Etiqueta etiqueta = etiquetaService.findOrCreateEtiqueta(requestDTO.getNombreEtiqueta().trim());
            empleadoService.asignarEtiquetaAUltiplesSubordinados(jefe.getId(), requestDTO.getEmpleadoIds(), etiqueta.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Etiqueta '" + etiqueta.getNombre() + "' asignada correctamente a los empleados seleccionados.");
        } catch (AccessDeniedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error de permisos: " + e.getMessage());
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error en procesarNuevoEtiquetado: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado al asignar la etiqueta: " + e.getMessage());
        }
        return "redirect:/empleados/gestion-etiquetas";
    }

    @PostMapping("/etiquetado/eliminar")
    public String procesarGestionEtiquetasEmpleado(@Valid @ModelAttribute("gestionarEtiquetasEmpleadoRequestDTO") GestionarEtiquetasEmpleadoRequestDTO requestDTO,
                                                   BindingResult bindingResult,
                                                   HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        Empleado jefe;
        try {
            jefe = obtenerJefeAutenticado(session);
        } catch (AccessDeniedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/usuarios/inicio-sesion";
        }

        if (bindingResult.hasErrors()) {
            popularModeloParaGestionEtiquetas(model, jefe);
            model.addAttribute("errorMessage", "Por favor, corrija los errores del formulario de gestión de etiquetas.");
            return "gestionEtiquetas";
        }

        try {
            empleadoService.actualizarEtiquetasSubordinado(jefe.getId(), requestDTO.getEmpleadoId(), requestDTO.getEtiquetaIdsAMantener());
            redirectAttributes.addFlashAttribute("successMessage", "Etiquetas actualizadas correctamente para el empleado.");
        } catch (AccessDeniedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error de permisos: " + e.getMessage());
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error en procesarGestionEtiquetasEmpleado: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado al actualizar etiquetas: " + e.getMessage());
        }
        return "redirect:/empleados/gestion-etiquetas";
    }


    @PostMapping("/etiquetado/masivo")
    public String procesarEtiquetadoMasivo(@Valid @ModelAttribute("etiquetadoMasivoRequestDTO") EtiquetadoMasivoRequestDTO requestDTO,
                                           BindingResult bindingResult,
                                           Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Empleado jefe;
        try {
            jefe = obtenerJefeAutenticado(session);
        } catch (AccessDeniedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/usuarios/inicio-sesion";
        }

        if (bindingResult.hasErrors() || requestDTO.getEmpleadoIds() == null || requestDTO.getEmpleadoIds().isEmpty() || requestDTO.getEtiquetaIds() == null || requestDTO.getEtiquetaIds().isEmpty()) {
            if (requestDTO.getEmpleadoIds() == null || requestDTO.getEmpleadoIds().isEmpty()) {
                bindingResult.rejectValue("empleadoIds", "NotEmpty", "Debe seleccionar al menos un empleado.");
            }
            if (requestDTO.getEtiquetaIds() == null || requestDTO.getEtiquetaIds().isEmpty()) {
                bindingResult.rejectValue("etiquetaIds", "NotEmpty", "Debe seleccionar al menos una etiqueta.");
            }
            popularModeloParaGestionEtiquetas(model, jefe);
            model.addAttribute("errorMessage", "Errores en el formulario de etiquetado masivo. Verifique los campos.");
            return "gestionEtiquetas";
        }

        try {
            // La autorización se hace dentro del servicio
            empleadoService.asignarEtiquetasMasivo(jefe.getId(), requestDTO.getEmpleadoIds(), requestDTO.getEtiquetaIds());
            redirectAttributes.addFlashAttribute("successMessage", "Etiquetas asignadas masivamente con éxito.");
        } catch (AccessDeniedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error de permisos: " + e.getMessage());
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error en los datos: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error en procesarEtiquetadoMasivo: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado durante el etiquetado masivo: " + e.getMessage());
        }
        return "redirect:/empleados/gestion-etiquetas";
    }


    // --- API ENDPOINTS ---

    @GetMapping("/etiquetado/api/empleado/{subordinadoId}/etiquetas")
    @ResponseBody
    public ResponseEntity<List<EtiquetaDTO>> obtenerEtiquetasDeEmpleado(@PathVariable UUID subordinadoId, HttpSession session) {
        Empleado jefe;
        try {
            jefe = obtenerJefeAutenticado(session);
            verificarJefeDirecto(jefe, subordinadoId);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

        Optional<Empleado> empleadoOpt = empleadoRepository.findById(subordinadoId);
        if (empleadoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<EtiquetaDTO> etiquetas = empleadoOpt.get().getEtiquetas().stream()
                .map(et -> modelMapper.map(et, EtiquetaDTO.class))
                .sorted(Comparator.comparing(EtiquetaDTO::getNombre))
                .collect(Collectors.toList());
        return ResponseEntity.ok(etiquetas);
    }

    @GetMapping("/consulta-productos")
    public String mostrarConsultaProductos(
            @ModelAttribute("criteriosBusquedaProductos") ProductoCriteriosBusquedaDTO criterios,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "descripcion") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model,
            HttpServletRequest request,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (session.getAttribute("emailAutenticado") == null && session.getAttribute("emailAutenticado") == null ) { // Ajusta el nombre del atributo de sesión
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para acceder a esta página.");
            return "redirect:/usuarios/inicio-sesion";
        }

        // Lista de campos válidos para ordenar Producto
        List<String> camposDeProductoValidos = Arrays.asList(
                "id", "descripcion", "precio", "marca",
                "categorias.nombre", "proveedor.nombre",
                "unidades", "valoracion", "fechaAlta", "esPerecedero"
        );

        if (!camposDeProductoValidos.contains(sortField)) {
            logger.warn("[EmpleadoController] CAMPO DE ORDENACIÓN NO VÁLIDO para productos: '{}'. Revirtiendo a 'descripcion'.", sortField);
            model.addAttribute("errorAlOrdenar", "El campo de ordenación '" + sortField + "' no es válido. Se ha ordenado por descripción.");
            sortField = "descripcion";
            sortDir = "asc";
        }

        String currentSortField = sortField;
        Sort.Direction currentSortDirection = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;

        String baseUrlApi = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String apiUrlProductos = baseUrlApi + "/api/empleado/productos";
        String apiUrlCategorias = baseUrlApi + "/api/empleado/productos/categorias";
        String apiUrlProveedores = baseUrlApi + "/api/empleado/productos/proveedores";


        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrlProductos)
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("sort", currentSortField + "," + (currentSortDirection == Sort.Direction.DESC ? "desc" : "asc"));

        if (criterios.getDescripcion() != null && !criterios.getDescripcion().isEmpty()) {
            builder.queryParam("descripcion", criterios.getDescripcion());
        }
        if (criterios.getCategoriaId() != null) {
            builder.queryParam("categoriaId", criterios.getCategoriaId().toString());
        }
        if (criterios.getPrecioMin() != null) {
            builder.queryParam("precioMin", criterios.getPrecioMin());
        }
        if (criterios.getPrecioMax() != null) {
            builder.queryParam("precioMax", criterios.getPrecioMax());
        }
        if (criterios.getProveedorIds() != null && !criterios.getProveedorIds().isEmpty()) {
            for (UUID proveedorId : criterios.getProveedorIds()) {
                builder.queryParam("proveedorIds", proveedorId.toString());
            }
            logger.info("[EmpleadoController] Añadiendo proveedorIds al API call: {}", criterios.getProveedorIds());
        }
        if (criterios.getEsPerecedero() != null) {
            builder.queryParam("esPerecedero", criterios.getEsPerecedero());
        }

        Page<ProductoResultadoDTO> paginaProductos = null;
        List<CategoriaSimpleDTO> categorias = Collections.emptyList();
        List<ProveedorSimpleDTO> proveedores = Collections.emptyList();

        try {
            String apiUrlCompletaProductos = builder.toUriString();

            // Llamada para obtener productos
            ResponseEntity<RestPage<ProductoResultadoDTO>> responseEntity = restTemplate.exchange(
                    apiUrlCompletaProductos,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<RestPage<ProductoResultadoDTO>>() {}
            );
            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                paginaProductos = responseEntity.getBody();
            } else {
                logger.error("[EmpleadoController] Error al cargar productos desde API admin. Código: {}", responseEntity.getStatusCode());
                paginaProductos = new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0);
                model.addAttribute("errorApi", "No se pudieron cargar los productos (código: " + responseEntity.getStatusCode() + ")");
            }

            // Llamada para obtener categorías
            ResponseEntity<List<CategoriaSimpleDTO>> responseCategorias = restTemplate.exchange(
                    apiUrlCategorias, HttpMethod.GET, null, new ParameterizedTypeReference<List<CategoriaSimpleDTO>>() {});
            if (responseCategorias.getStatusCode().is2xxSuccessful()) {
                categorias = responseCategorias.getBody();
            } else {
                model.addAttribute("errorApiFiltros", "No se pudieron cargar las categorías.");
            }

            // Llamada para obtener proveedores
            ResponseEntity<List<ProveedorSimpleDTO>> responseProveedores = restTemplate.exchange(
                    apiUrlProveedores, HttpMethod.GET, null, new ParameterizedTypeReference<List<ProveedorSimpleDTO>>() {});
            if (responseProveedores.getStatusCode().is2xxSuccessful()) {
                proveedores = responseProveedores.getBody();
            } else {
                model.addAttribute("errorApiFiltros", (model.containsAttribute("errorApiFiltros") ? model.getAttribute("errorApiFiltros") + " " : "") + "No se pudieron cargar los proveedores.");
            }

        } catch (Exception e) {
            logger.error("[EmpleadoController] Excepción al llamar a la API de productos: {}", e.getMessage(), e);
            paginaProductos = new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0);
            model.addAttribute("errorApi", "Error al conectar con el servicio de productos: " + e.getMessage());
        }

        model.addAttribute("paginaProductos", paginaProductos);
        model.addAttribute("categorias", categorias);
        model.addAttribute("proveedores", proveedores);
        model.addAttribute("estadoActualSortField", currentSortField); // Para la lógica de los enlaces de ordenación
        model.addAttribute("estadoActualSortDir", (currentSortDirection == Sort.Direction.DESC ? "desc" : "asc"));

        return "consultaProductos";
    }

    /**
     * Muestra la página de detalle de un producto para empleados.
     * @param id El UUID del producto a mostrar.
     * @param model Modelo para pasar datos a la vista.
     * @param session Sesión HTTP para control de acceso.
     * @param redirectAttributes Atributos para redirección.
     * @return Nombre de la plantilla Thymeleaf para el detalle del producto o redirección.
     */
    @GetMapping("/productos/detalle/{id}")
    public String mostrarDetalleProductoEmpleado(@PathVariable UUID id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("emailAutenticado") == null && session.getAttribute("emailAutenticado") == null ) { // Ajusta el nombre del atributo de sesión
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para acceder a esta página.");
            return "redirect:/usuarios/inicio-sesion";
        }

        Optional<Producto> productoOpt = productoService.obtenerProductoPorId(id);

        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            model.addAttribute("producto", producto);

            if (producto instanceof Libro) {
                model.addAttribute("tipoProducto", "Libro");
                model.addAttribute("libro", (Libro) producto);
            } else if (producto instanceof Mueble) {
                model.addAttribute("tipoProducto", "Mueble");
                model.addAttribute("mueble", (Mueble) producto);
            } else if (producto instanceof Ropa) {
                model.addAttribute("tipoProducto", "Ropa");
                model.addAttribute("ropa", (Ropa) producto);
            } else {
                model.addAttribute("tipoProducto", "Desconocido");
            }
            return "detalleProducto";
        } else {
            redirectAttributes.addFlashAttribute("error", "Producto no encontrado con ID: " + id);
            return "redirect:/empleados/consulta-productos";
        }
    }

    private Empleado obtenerJefeAutenticado(HttpSession session) throws AccessDeniedException {
        String emailJefe = (String) session.getAttribute("emailAutenticado");
        if (emailJefe == null) {
            throw new AccessDeniedException("Usuario no autenticado.");
        }
        Usuario usuarioJefe = usuarioRepository.findByEmail(emailJefe)
                .orElseThrow(() -> new AccessDeniedException("Usuario jefe no encontrado con email: " + emailJefe));
        return (Empleado) empleadoRepository.findByUsuarioId(usuarioJefe.getId())
                .orElseThrow(() -> new AccessDeniedException("Perfil de empleado (jefe) no encontrado para el usuario: " + emailJefe));
    }

    private void verificarJefeDirecto(Empleado jefe, UUID subordinadoId) throws AccessDeniedException {
        Empleado subordinado = empleadoRepository.findById(subordinadoId)
                .orElseThrow(() -> new EntityNotFoundException("Subordinado no encontrado con ID: " + subordinadoId));
        if (subordinado.getJefe() == null || !subordinado.getJefe().getId().equals(jefe.getId())) {
            throw new AccessDeniedException("Acceso denegado: No es jefe directo del empleado seleccionado.");
        }
    }

    private void popularModeloParaGestionEtiquetas(Model model, Empleado jefe) {
        List<Empleado> subordinados = empleadoRepository.findByJefeIdAndActivoTrue(jefe.getId());
        List<EmpleadoSimpleDTO> subordinadosDTO = subordinados.stream()
                .map(e -> modelMapper.map(e, EmpleadoSimpleDTO.class))
                .collect(Collectors.toList());
        model.addAttribute("subordinados", subordinadosDTO);

        List<EtiquetaDTO> todasLasEtiquetasDTO = etiquetaService.findAllDTO();
        model.addAttribute("etiquetasParaMasivo", todasLasEtiquetasDTO);

        if (!model.containsAttribute("asignarEtiquetaRequestDTO")) {
            model.addAttribute("asignarEtiquetaRequestDTO", new AsignarEtiquetaRequestDTO());
        }
        if (!model.containsAttribute("gestionarEtiquetasEmpleadoRequestDTO")) {
            model.addAttribute("gestionarEtiquetasEmpleadoRequestDTO", new GestionarEtiquetasEmpleadoRequestDTO());
        }
        if (!model.containsAttribute("etiquetadoMasivoRequestDTO")) {
            model.addAttribute("etiquetadoMasivoRequestDTO", new EtiquetadoMasivoRequestDTO());
        }
    }

}
