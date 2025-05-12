package org.grupo_h.administracion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.*;
import org.grupo_h.administracion.dto.EmpleadoConsultaDTO;
import org.grupo_h.administracion.dto.EmpleadoDTO;
import org.grupo_h.administracion.dto.EmpleadoDetalleDTO;
import org.grupo_h.administracion.dto.EmpleadoSimpleDTO;
import org.grupo_h.administracion.service.*;
import org.grupo_h.administracion.dto.*;
import org.grupo_h.administracion.auxiliar.RestPage;
import org.grupo_h.comun.entity.Administrador;
import org.grupo_h.administracion.service.ParametrosService;
import org.grupo_h.administracion.service.AdministradorService;
import org.grupo_h.comun.entity.Departamento;
import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.comun.entity.Usuario;
import org.grupo_h.comun.repository.AdministradorRepository;
import org.grupo_h.comun.repository.DepartamentoRepository;
import org.grupo_h.comun.repository.EmpleadoRepository;
import org.grupo_h.comun.repository.UsuarioRepository;
import org.grupo_h.comun.service.DepartamentoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador para gestionar las operaciones relacionadas con los administradores.
 */
@Controller
@RequestMapping("/administrador")
public class AdministradorController {

    private final AdministradorService administradorService;
    private final AdministradorRepository administradorRepository;
    private final EmpleadoService empleadoService;
    private final ParametrosService parametrosService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final DepartamentoService departamentoService;
    private final EmpleadoRepository empleadoRepository;
    private final DepartamentoRepository departamentoRepository;
    private final RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(AdministradorController.class);


    @Autowired
    public AdministradorController(AdministradorService administradorService,
                                   AdministradorRepository administradorRepository,
                                   EmpleadoService empleadoService,
                                   ParametrosService parametrosService,
                                   BCryptPasswordEncoder passwordEncoder,
                                   UsuarioRepository usuarioRepository,
                                   UsuarioService usuarioService,
                                   DepartamentoService departamentoService,
                                   EmpleadoRepository empleadoRepository,
                                   DepartamentoRepository departamentoRepository,
                                   RestTemplate restTemplate) {
        this.administradorService = administradorService;
        this.administradorRepository = administradorRepository;
        this.empleadoService = empleadoService;
        this.parametrosService = parametrosService;
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.departamentoService = departamentoService;
        this.empleadoRepository = empleadoRepository;
        this.departamentoRepository = departamentoRepository;
        this.restTemplate = restTemplate;
    }


    /**
     * Muestra el formulario de inicio de sesión.
     *
     * @param model         Modelo para pasar datos a la vista.
     * @param error         Parámetro de error, si existe.
     * @param logout        Parámetro de logout, si existe.
     * @param logoutReferer Referer de la página de logout.
     * @return Nombre de la vista de autenticación.
     */
    @GetMapping("/inicio-sesion")
    public String mostrarFormularioAdministrador(Model model,
                                                 HttpServletRequest request,
                                                 @RequestParam(value = "error", required = false) String error,
                                                 @RequestParam(value = "logout", required = false) String logout,
                                                 @ModelAttribute("logoutReferer") String logoutReferer) {
        List<String> loginsAnteriores = new ArrayList<>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                // Si existe una cookie con los datos de logins anteriores
                if ("loginsAnterioresAdmin".equals(cookie.getName())) {
                    String cookieValue = cookie.getValue();
                    // Comprobar que el valor de la cookie no sea nulo ni esté vacío antes de procesar
                    if (cookieValue != null && !cookieValue.isEmpty()) {
                        try {
                            String decodedValue = new String(Base64.getUrlDecoder().decode(cookieValue));
                            // Dividir Y FILTRAR cadenas vacías o nulas
                            loginsAnteriores = Arrays.stream(decodedValue.split(","))
                                    .map(String::trim) // Opcional: quitar espacios blancos alrededor
                                    .filter(email -> email != null && !email.isEmpty()) // <-- Filtrar vacíos
                                    .collect(Collectors.toList()); // Recolectar en la lista
                        } catch (IllegalArgumentException e) {
                            // Manejar posible error en Base64 si la cookie está corrupta
                            System.err.println("Error al decodificar cookie loginsAnterioresAdmin: " + e.getMessage());
                            // Dejar loginsAnteriores vacía
                        }
                    }
                    break; // Encontrada la cookie, salir del bucle
                }
            }
        }
        model.addAttribute("loginsAnterioresAdmin", loginsAnteriores);

        // Si el atributo de logout no es nulo, mostramos el mensaje de Logout
        if (logout != null) {
            model.addAttribute("mensaje", "Has cerrado sesión exitosamente.");
            if (logoutReferer != null && !logoutReferer.isEmpty()) {
                model.addAttribute("showLogoutReferer", true);
                model.addAttribute("logoutRefererUrl", logoutReferer);
                model.addAttribute("logoutRefererText", obtenerUrl(logoutReferer));
            }
        }

        model.addAttribute("pedirEmail", true);
        return "autenticacionPorPasos";
    }

    /**
     * Procesa el email de administrador para el inicio de sesión.
     *
     * @param email              Email de administrador introducido.
     * @param session            Sesión HTTP.
     * @param redirectAttributes Atributos para redirección.
     * @return Redirección al formulario de contraseña o de error.
     */
    @PostMapping("/inicio-sesion/administrador")
    public String procesarAdministrador(@RequestParam String email,
                                        HttpServletRequest request,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes) {

        // --- Comprobación Remember Me ---
        Cookie[] cookies = request.getCookies();
        String rememberMeTokenValue = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("remember-me-token-admin".equals(cookie.getName())) {
                    rememberMeTokenValue = cookie.getValue();
                    break;
                }
            }
        }

        if (rememberMeTokenValue != null && !rememberMeTokenValue.isEmpty()) {
            System.out.println("Encontrada cookie remember-me-token-admin: " + rememberMeTokenValue);
            Optional<Administrador> administradorOptByToken =
                    administradorRepository.findByRememberMeToken(rememberMeTokenValue);

            if (administradorOptByToken.isPresent()) {
                Administrador administradorRecordado = administradorOptByToken.get();
                System.out.println("Administrador encontrado por token: " + administradorRecordado.getEmail());
                if (administradorRecordado.getEmail().equalsIgnoreCase(email) &&
                        administradorRecordado.getRememberMeTokenExpiry() != null &&
                        administradorRecordado.getRememberMeTokenExpiry().isAfter(LocalDateTime.now()) &&
                        administradorRecordado.isHabilitado() && !administradorRecordado.isCuentaBloqueada()) {

                    System.out.println("Token válido para " + email + ". Re-autenticando.");
                    session.setAttribute("emailAutenticadoAdmin", administradorRecordado.getEmail());
                    Integer contadorActual = (Integer) session.getAttribute("contadorConexionesAdmin");
                    int nuevoContador = (contadorActual == null) ? 1 : contadorActual + 1;
                    session.setAttribute("contadorConexionesAdmin", nuevoContador);
                    session.setAttribute("userAgentAdmin", request.getHeader("User-Agent"));
                    administradorRecordado.setSesionesTotales(administradorRecordado.getSesionesTotales() + 1);
                    administradorRepository.save(administradorRecordado);
                    redirectAttributes.addFlashAttribute("contadorConexionesFlash", nuevoContador);

                    return "redirect:/administrador/info";
                } else {
                    System.out.println("Token inválido (expirado, email no coincide, cuenta no activa) para: " + email);
                    // Token inválido, expirado, email no coincide o cuenta bloqueada/deshabilitada.
                    if (administradorRecordado.getRememberMeToken() != null) {
                        administradorRecordado.setRememberMeToken(null);
                        administradorRecordado.setRememberMeTokenExpiry(null);
                        administradorRepository.save(administradorRecordado);
                    }
                    Cookie removeCookie = new Cookie("remember-me-token-admin", "");
                    removeCookie.setPath("/");
                    removeCookie.setMaxAge(0);
                }
            } else {
                System.out.println("Token de cookie no encontrado en BD.");
                Cookie removeCookie = new Cookie("remember-me-token-admin", "");
                removeCookie.setPath("/");
                removeCookie.setMaxAge(0);
            }
        }
        // --- Fin Comprobación Remember Me ---


        // Comprobamos que el administrador que nos proporciona el usuario con email existe
        Optional<Administrador> administradorOpt = administradorRepository.findByEmail(email);

        // SI NO EXISTE
        if (administradorOpt.isEmpty() || !administradorOpt.get().isHabilitado()) {
            redirectAttributes.addFlashAttribute("error", "Administrador no encontrado o deshabilitado");
            return "redirect:/administrador/inicio-sesion?error=true";
        }

        // SI EXISTE
        Administrador administrador = administradorOpt.get();

        if (administrador.isCuentaBloqueada()) {
            LocalDateTime horaDesbloqueo = administrador.getTiempoHastaDesbloqueo();
            // SI ESTA BLOQUEADO Y NO SE HA PASADO EL TIEMPO
            if (horaDesbloqueo != null && LocalDateTime.now().isBefore(horaDesbloqueo)) {
                // Todavía bloqueada: Mostrar mensaje específico
                DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                        .withLocale(Locale.of("es", "ES"));
                String unlockTimeString = horaDesbloqueo.format(formatter);
                String mensajeError = "Su cuenta está bloqueada temporalmente. Podrá intentar de nuevo después de las " + unlockTimeString;
                redirectAttributes.addFlashAttribute("error", mensajeError);
                return "redirect:/administrador/inicio-sesion?error=true";
            } else {
                // SI ESTA BLOQUEADO Y YA SE HA PASADO EL TIEMPO
                administradorService.desbloquearCuenta(email);
            }
        }

        // LO AÑADIMOS COMO ATRIBUTO A LA SESION PARA EL SIGUIENTE PASO
        session.setAttribute("emailParaLoginAdmin", email);
        return "redirect:/administrador/inicio-sesion/password";
    }

    /**
     * Muestra el formulario de contraseña para el inicio de sesión.
     *
     * @param model              Modelo para pasar datos a la vista.
     * @param session            Sesión HTTP.
     * @param redirectAttributes Atributos para redirección.
     * @return Nombre de la vista de autenticación.
     */
    @GetMapping("/inicio-sesion/password")
    public String mostrarFormularioPassword(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String email = (String) session.getAttribute("emailParaLoginAdmin");

        if (email == null) {
            redirectAttributes.addFlashAttribute("error", "Por favor, introduce primero tu email.");
            return "redirect:/administrador/inicio-sesion";
        }

        model.addAttribute("email", email);
        model.addAttribute("pedirPassword", true);
        return "autenticacionPorPasos";
    }

    /**
     * Procesa la contraseña y autentica al administrador.
     *
     * @param contrasena         Contraseña introducida.
     * @param request            Solicitud HTTP.
     * @param session            Sesión HTTP.
     * @param redirectAttributes Atributos para redirección.
     * @return Redirección a la página de información o de error.
     */
    @PostMapping("/inicio-sesion/autenticar")
    public String autenticarManualConPassword(@RequestParam String contrasena,
                                              HttpServletRequest request,
                                              HttpServletResponse response,
                                              HttpSession session,
                                              RedirectAttributes redirectAttributes) {
        String email = (String) session.getAttribute("emailParaLoginAdmin");

        if (email == null) {
            redirectAttributes.addFlashAttribute("error",
                    "Error de sesión. Por favor, inicia sesión de nuevo.");
            return "redirect:/administrador/inicio-sesion";
        }

        Optional<Administrador> administradorOpt = administradorRepository.findByEmail(email);

        if (administradorOpt.isEmpty()) {
            session.removeAttribute("emailParaLoginAdmin");
            redirectAttributes.addFlashAttribute("error", "Email no encontrado.");
            return "redirect:/administrador/inicio-sesion?error=true";
        }

        Administrador administrador = administradorOpt.get();

        // A la hora de autenticarse con el servidor hay un error, este booleano confirma la redireccion como necesaria
        boolean debeRedirigirPorErrorPrevio = false;
        String mensajeErrorPrevio = null;
        if (!administrador.isHabilitado()) {
            mensajeErrorPrevio = "La cuenta no está disponible (deshabilitada).";
            debeRedirigirPorErrorPrevio = true;
        } else if (administrador.isCuentaBloqueada()) {
            LocalDateTime horaDesbloqueo = administrador.getTiempoHastaDesbloqueo();
            // SI LA HORA ES ANTIGUA, SE DEJA BLOQUEADO Y SE COMUNICA CUANDO SE DESBLOQUEARA
            if (horaDesbloqueo != null && LocalDateTime.now().isBefore(horaDesbloqueo)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                        .withLocale(Locale.of("es", "ES"));
                String unlockTimeString = horaDesbloqueo.format(formatter);
                mensajeErrorPrevio = "Su cuenta está bloqueada temporalmente. Podrá intentar de nuevo después de las "
                        + unlockTimeString;
                debeRedirigirPorErrorPrevio = true;
            } else {
                // SI LA HORA YA PASO, SE ABRE
                administradorService.desbloquearCuenta(email);
                administrador = administradorRepository.findByEmail(email).orElse(administrador);
            }
        }

        if (debeRedirigirPorErrorPrevio) {
            session.removeAttribute("emailParaLoginAdmin");
            redirectAttributes.addFlashAttribute("error", mensajeErrorPrevio);
            return "redirect:/administrador/inicio-sesion?error=true";
        }

        // SI LA CONTRASEÑA COINCIDE CON LA DADA POR EL ADMINISTRADOR
        if (passwordEncoder.matches(contrasena, administrador.getContrasena())) {
            // --- Inicio: Lógica de Cookies ---
            Set<String> logins = new LinkedHashSet<>();
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("loginsAnterioresAdmin".equals(cookie.getName())) {
                        String decodedValue = new String(Base64.getUrlDecoder().decode(cookie.getValue()));
                        logins.addAll(Arrays.asList(decodedValue.split(",")));
                        break;
                    }
                }
            }
            logins.add(email);

            String joinedLogins = String.join(",", logins);
            String encodedValue = Base64.getUrlEncoder().withoutPadding().encodeToString(joinedLogins.getBytes());

            Cookie loginCookie = new Cookie("loginsAnterioresAdmin", encodedValue);
            loginCookie.setPath("/");
            loginCookie.setMaxAge(60 * 60 * 24 * 30); //30 días de duración
            loginCookie.setHttpOnly(true);
            response.addCookie(loginCookie);
            // --- Fin: Lógica de Cookies ---

            administrador.setSesionesTotales(administrador.getSesionesTotales() + 1);
            if (administrador.getIntentosFallidos() > 0) {
                administrador.setIntentosFallidos(0);
            }
            administradorRepository.save(administrador);

            session.removeAttribute("emailParaLoginAdmin");

            Integer contadorActual = (Integer) session.getAttribute("contadorConexionesAdmin");
            int nuevoContador = (contadorActual == null) ? 1 : contadorActual + 1;
            session.setAttribute("contadorConexionesAdmin", nuevoContador);

            redirectAttributes.addFlashAttribute("contadorConexionesFlash", nuevoContador);

            session.setAttribute("emailAutenticadoAdmin", administrador.getEmail());
            session.setAttribute("contadorConexionesAdmin", 1);
            session.setAttribute("userAgentAdmin", request.getHeader("User-Agent"));

            // --- Lógica Remember Me ---
            try {
                String rememberMeTokenValue = UUID.randomUUID().toString();
                // Duración de la cookie/token (14 días)
                LocalDateTime rememberMeExpiry = LocalDateTime.now().plusDays(14);

                administrador.setRememberMeToken(rememberMeTokenValue);
                administrador.setRememberMeTokenExpiry(rememberMeExpiry);
                administradorRepository.save(administrador); // Guardar token en BD

                Cookie rememberMeCookie = new Cookie("remember-me-token-admin", rememberMeTokenValue);
                rememberMeCookie.setPath("/");
                rememberMeCookie.setMaxAge(14 * 24 * 60 * 60); // 14 días
                rememberMeCookie.setHttpOnly(true);
                response.addCookie(rememberMeCookie);
                System.out.println("Cookie remember-me-token-admin creada para: " + email);

            } catch (Exception e) {
                System.err.println("Error al crear cookie remember-me-token-admin: " + e.getMessage());
            }
            // --- Fin Lógica Remember Me ---

            return "redirect:/administrador/info";
        } else {
            // Llama al servicio para manejar el fallo
            administradorService.procesarLoginFallido(email);
            // Comprueba si la cuenta está ahora bloqueada para el mensaje de error
            Administrador administradorActualizado = administradorRepository.findByEmail(email).orElse(administrador); // Recarga para obtener estado actualizado
            String mensajeErrorFallido;
            int maxIntentos = parametrosService.getMaxIntentosFallidos();
            if (administradorActualizado.isCuentaBloqueada()) {
                // ¡LA CUENTA SE ACABA DE BLOQUEAR!
                LocalDateTime horaDesbloqueo = administradorActualizado.getTiempoHastaDesbloqueo();
                if (horaDesbloqueo != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                            .withLocale(Locale.of("es", "ES"));
                    String unlockTimeString = horaDesbloqueo.format(formatter);
                    mensajeErrorFallido = "Contraseña incorrecta. Cuenta bloqueada. Podrá intentar de nuevo después de las " + unlockTimeString;
                } else {
                    mensajeErrorFallido = "Contraseña incorrecta. Cuenta bloqueada."; // Fallback
                }

                redirectAttributes.addFlashAttribute("error", mensajeErrorFallido);
                session.removeAttribute("emailParaLoginAdmin"); // Quitar email de la sesión
                // Redirigir a la página de EMAIL (inicio-sesion)
                return "redirect:/administrador/inicio-sesion?error=true";

            } else {
                // LA CUENTA NO SE BLOQUEÓ (aún quedan intentos)
                mensajeErrorFallido = "Contraseña incorrecta. Intentos restantes: " + (maxIntentos - administradorActualizado.getIntentosFallidos());
                redirectAttributes.addFlashAttribute("error", mensajeErrorFallido);
                // Redirigir a la página de CONTRASEÑA (como antes)
                return "redirect:/administrador/inicio-sesion/password?error=true";
            }
        }
    }

    /**
     * Elimina un email de administrador de la cookie de logins anteriores.
     *
     * @param emailAEliminar Email a eliminar de la cookie.
     * @param request        Solicitud HTTP.
     * @param response       Respuesta HTTP.
     * @return Respuesta indicando éxito o fracaso de la operación.
     */
    @PostMapping("/inicio-sesion/eliminar-administrador")
    public ResponseEntity<?> eliminarAdministradorCookie(@RequestParam String emailAEliminar,
                                                         HttpServletRequest request,
                                                         HttpServletResponse response) {
        Set<String> logins = new LinkedHashSet<>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("loginsAnterioresAdmin".equals(cookie.getName())) {
                    String decodedValue = new String(Base64.getUrlDecoder().decode(cookie.getValue()));
                    logins.addAll(Arrays.asList(decodedValue.split(",")));
                    break;
                }
            }
        }

        boolean removed = logins.remove(emailAEliminar); // Intentar eliminar

        if (removed) {
            String joinedLogins = String.join(",", logins);
            String encodedValue = Base64.getUrlEncoder().withoutPadding().encodeToString(joinedLogins.getBytes());

            Cookie loginCookie = new Cookie("loginsAnterioresAdmin", encodedValue);
            loginCookie.setPath("/");
            loginCookie.setMaxAge(60 * 60 * 24 * 30); // Actualizar duración
            loginCookie.setHttpOnly(true);
            response.addCookie(loginCookie);
            return ResponseEntity.ok().build(); // Éxito
        } else {
            // Opcional: devolver error si no se encontró
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Muestra la información del administrador autenticado.
     *
     * @param model              Modelo para pasar datos a la vista.
     * @param session            Sesión HTTP.
     * @param redirectAttributes Atributos para redirección.
     * @return Nombre de la vista de información del administrador.
     */
    @GetMapping("/info")
    public String mostrarInformacionAdministrador(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String email = (String) session.getAttribute("emailAutenticadoAdmin");

        Integer contadorSesion = null;

        if (model.containsAttribute("contadorConexionesFlash")) {
            try {
                contadorSesion = (Integer) model.getAttribute("contadorConexionesFlash");
                if (contadorSesion != null) {
                    session.setAttribute("contadorConexionesAdmin", contadorSesion);
                }
            } catch (Exception e) {
                Object contadorObj = session.getAttribute("contadorConexionesAdmin");
                if (contadorObj instanceof Integer) {
                    contadorSesion = (Integer) contadorObj;
                }
            }
        } else {
            Object contadorObj = session.getAttribute("contadorConexionesAdmin");
            if (contadorObj instanceof Integer) {
                contadorSesion = (Integer) contadorObj;
            }
        }

        if (email == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para ver esta información.");
            return "redirect:/administrador/inicio-sesion";
        }

        Optional<Administrador> administradorOpt = administradorRepository.findByEmail(email);
        if (administradorOpt.isEmpty()) {
            session.invalidate();
            redirectAttributes.addFlashAttribute("error", "Error al recuperar datos del administrador.");
            return "redirect:/administrador/inicio-sesion";
        }

        Administrador administrador = administradorOpt.get();
        String userAgentAdmin = (String) session.getAttribute("userAgentAdmin");

        model.addAttribute("email", email);
        model.addAttribute("totalLoginsAdministrador", administrador.getSesionesTotales());
        model.addAttribute("loginsSesionActualAdmin", contadorSesion != null ? contadorSesion : 0);
        model.addAttribute("navegadorActualAdmin", userAgentAdmin != null ? userAgentAdmin : "No disponible");

        return "areaPersonal";
    }

    /**
     * Cierra la sesión del administrador.
     *
     * @param request            Solicitud HTTP.
     * @param response           Respuesta HTTP para manipular cookies.
     * @param redirectAttributes Atributos para redirección.
     * @return Redirección a la página de inicio de sesión.
     */
    @GetMapping("/logout")
    public String logoutManual(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        HttpSession session = request.getSession(false);
        String referer = request.getHeader("Referer");

        // --- Limpiar Remember Me ---
        String email = (session != null) ? (String) session.getAttribute("emailAutenticadoAdmin") : null;
        if (email != null) {
            Optional<Administrador> administradorOpt = administradorRepository.findByEmail(email);
            if (administradorOpt.isPresent()) {
                Administrador administrador = administradorOpt.get();
                administrador.setRememberMeToken(null);
                administrador.setRememberMeTokenExpiry(null);
                administradorRepository.save(administrador);
                System.out.println("Token remember-me-token-admin limpiado para: " + email);
            }
        }
        // Borrar la cookie
        Cookie removeCookie = new Cookie("remember-me-token-admin", "");
        removeCookie.setPath("/");
        removeCookie.setMaxAge(0);
        response.addCookie(removeCookie);
        // --- Fin Limpiar Remember Me ---

        if (session != null) {
            session.invalidate();
        }

        if (referer != null && !referer.contains("/inicio-sesion")) {
            redirectAttributes.addFlashAttribute("logoutReferer", referer);
        }

        return "redirect:/administrador/inicio-sesion?logout=true";
    }

    /**
     * Recupera la contraseña de un administrador (¡Inseguro!).
     *
     * @param email Email del administrador.
     * @return Respuesta con la contraseña o mensaje de error.
     */
    @GetMapping("/recuperar-contraseña")
    @ResponseBody
    public ResponseEntity<String> recuperarContrasenia(
            @RequestParam(value = "email", required = false) String email) {

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Falta el parámetro 'administrador'");
        }

        Optional<Administrador> userOpt = administradorRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Administrador no encontrado");
        }

        return ResponseEntity.ok(administradorRepository.findByEmail(email).get().getContrasena());
    }

    /**
     * Método auxiliar para obtener la URL de un referer.
     *
     * @param url URL del referer.
     * @return URL formateada.
     */
    private String obtenerUrl(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }
        return url;
    }

    /* ------------------------ CAMBIO A DETALLE ----------------------------- */

    /**
     * Obtiene y muestra el detalle de un empleado específico.
     *
     * @param id    Identificador único del empleado a consultar
     * @param model Modelo para transferir datos a la vista
     * @return Nombre de la vista a renderizar (detalleEmpleado)
     */
    // TODO: REFACTOR
    @GetMapping("/detalle/{id}")
    public String obtenerDetalleEmpleado(@PathVariable UUID id, Model model) {
        Optional<EmpleadoDetalleDTO> empleadoOpt = empleadoService.obtenerDetalleEmpleado(id);
        if (empleadoOpt.isPresent()) {
            model.addAttribute("empleado", empleadoOpt.get());
            return "detalleEmpleado"; // Plantilla Thymeleaf
        } else {
            // TODO: Crear una pagina de error
            return "detalleEmpleado"; // Página de error
        }
    }

    /* ------------------------ CAMBIO A BLOQUEO / DESBLOQUEO ----------------------------- */

    /**
     * Muestra el dashboard de gestión de usuarios con filtrado opcional.
     *
     * @param filtro Filtro de búsqueda opcional para usuarios.
     * @param model  Modelo para pasar datos a la vista.
     * @return Vista del dashboard de usuarios.
     */
    @GetMapping("/dashboardGestionUsuarios")
    public String mostrarDashboard(
            @RequestParam(value = "filtro", required = false) String filtro,
            Model model) {
        List<Usuario> usuarios;
        if (filtro != null && !filtro.isEmpty()) {
            usuarios = usuarioService.buscarPorFiltro(filtro);
        } else {
            usuarios = usuarioService.obtenerTodosLosUsuarios();
        }
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("filtro", filtro);
        return "dashboardUsuarios";
    }

    /**
     * Bloquea un usuario desde la administración.
     *
     * @param id            ID del usuario a bloquear.
     * @param motivoBloqueo Motivo por el cual se bloquea al usuario.
     * @return Redirección al dashboard de gestión de usuarios.
     */
    @PostMapping("/bloquear-usuario")
    public String bloquearUsuario(
            @RequestParam UUID id,
            @RequestParam String motivoBloqueo) {
        usuarioService.bloquearUsuarioAdmin(id, motivoBloqueo, parametrosService.getDuracionBloqueoMinutosAdmin());
        return "redirect:/administrador/dashboardGestionUsuarios";
    }

    /**
     * Desbloquea un usuario bloqueado.
     *
     * @param id ID del usuario a desbloquear.
     * @return Redirección al dashboard de gestión de usuarios.
     */
    @PostMapping("/desbloquear-usuario")
    public String desbloquearUsuario(@RequestParam UUID id) {
        usuarioService.desbloquearUsuario(id);
        return "redirect:/administrador/dashboardGestionUsuarios";
    }

    /**
     * Muestra el formulario de edición de un usuario.
     *
     * @param id    ID del usuario a editar.
     * @param model Modelo para pasar datos a la vista.
     * @return Vista del formulario de edición o redirección al dashboard.
     */
    @GetMapping("/editar-usuario/{id}")
    public String mostrarFormularioEdicion(@PathVariable UUID id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id);
        if (usuario != null) {
            model.addAttribute("usuario", usuario);
            return "editarUsuario";
        }
        return "redirect:/administrador/dashboardGestionUsuarios";
    }

    /**
     * Procesa la edición de un usuario, incluyendo cambio opcional de contraseña.
     *
     * @param usuario             Datos del usuario a actualizar.
     * @param nuevaContrasena     Nueva contraseña (opcional).
     * @param confirmarContrasena Confirmación de la nueva contraseña.
     * @param redirectAttributes  Atributos para redirección.
     * @return Redirección al dashboard o al formulario de edición en caso de error.
     */
    @PostMapping("/editar-usuario")
    public String editarUsuario(
            @ModelAttribute Usuario usuario,
            @RequestParam(required = false) String nuevaContrasena,
            @RequestParam(required = false) String confirmarContrasena,
            RedirectAttributes redirectAttributes) {

        // Validar si se proporcionó una nueva contraseña
        if (nuevaContrasena != null && !nuevaContrasena.isEmpty()) {
            // Verificar que las contraseñas coincidan
            if (!nuevaContrasena.equals(confirmarContrasena)) {
                redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden.");
                return "redirect:/administrador/editar-usuario/" + usuario.getId();
            }
            // Encriptar la nueva contraseña
            usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));
        } else {
            // Si no se proporciona una nueva contraseña, mantener la contraseña actual
            Usuario usuarioExistente = usuarioService.buscarPorId(usuario.getId());
            if (usuarioExistente != null) {
                usuario.setContrasena(usuarioExistente.getContrasena());
            }
        }

        // Guardar los cambios
        usuarioService.actualizarUsuario(usuario);
        redirectAttributes.addFlashAttribute("mensaje", "Usuario actualizado exitosamente.");
        return "redirect:/administrador/dashboardGestionUsuarios";
    }

    /* ------------------------ GESTION DE SUBORDINADOS ----------------------------- */

    /**
     * Muestra el formulario para asignar subordinados a un jefe.
     *
     * @param model Modelo para pasar datos a la vista.
     * @return Nombre de la plantilla Thymeleaf.
     */
    @GetMapping("/gestion-subordinados")
    public String mostrarFormularioAsignarSubordinados(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("emailAutenticadoAdmin") == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para acceder a esta página.");
            return "redirect:/administrador/inicio-sesion";
        }
        List<EmpleadoSimpleDTO> todosLosEmpleados = empleadoService.obtenerTodosLosEmpleadosParaSeleccion();
        model.addAttribute("empleados", todosLosEmpleados);
        return "gestionSubordinados";
    }

    /**
     * Procesa la asignación de un subordinado a un jefe.
     *
     * @param idJefe             ID del empleado jefe.
     * @param idSubordinado      ID del empleado subordinado.
     * @param redirectAttributes Atributos para pasar mensajes entre redirecciones.
     * @return Redirección a la página de gestión de subordinados.
     */
    @PostMapping("/asignar-subordinado")
    public String asignarSubordinado(
            @RequestParam UUID idJefe,
            @RequestParam UUID idSubordinado,
            RedirectAttributes redirectAttributes) {
        try {
            empleadoService.asignarSubordinado(idJefe, idSubordinado);
            redirectAttributes.addFlashAttribute("mensajeExito", "Subordinado asignado correctamente al jefe.");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al asignar: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al asignar: " + e.getMessage());
        } catch (Exception e) {
//             logger.error("Error inesperado al asignar subordinado:", e);
            redirectAttributes.addFlashAttribute("mensajeError", "Ocurrió un error inesperado al procesar la solicitud.");
        }
        return "redirect:/administrador/gestion-subordinados";
    }

    /**
     * Procesa la desasignación de un empleado de su jefe actual.
     *
     * @param idSubordinadoADesasignar ID del empleado a desasignar.
     * @param session                  La sesión HTTP actual (para control de acceso manual).
     * @param redirectAttributes       Atributos para pasar mensajes entre redirecciones.
     * @return Redirección a la página de gestión de subordinados.
     */
    @PostMapping("desasignar-subordinado")
    public String desasignarSubordinadoWeb(
            @RequestParam UUID idSubordinadoADesasignar,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (session.getAttribute("emailAutenticadoAdmin") == null) {
            redirectAttributes.addFlashAttribute("error", "Tu sesión ha expirado o no has iniciado sesión.");
            return "redirect:/administrador/inicio-sesion";
        }

        try {
            empleadoService.desasignarSubordinadoDeSuJefe(idSubordinadoADesasignar);
            redirectAttributes.addFlashAttribute("mensajeExito", "Subordinado desasignado correctamente de su jefe.");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al desasignar: " + e.getMessage());
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al desasignar: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Ocurrió un error inesperado al procesar la solicitud de desasignación.");
        }
        return "redirect:/administrador/gestion-subordinados";
    }



    /* ------------------------ GESTION DE PRODUCTOS ----------------------------- */

    @GetMapping("/importar-catalogo")
    public String vistaImportarCatalogo(HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("emailAutenticadoAdmin") == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para acceder a esta página.");
            return "redirect:/administrador/inicio-sesion";
        }
        return "importarCatalogo";
    }

    @GetMapping("/consulta-productos")
    public String mostrarVistaProductos(
            @ModelAttribute("criteriosBusqueda") ProductoCriteriosBusquedaDTO criterios,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "descripcion") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model,
            HttpServletRequest request,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        logger.info("[AdminController] Petición inicial: sortField='{}', sortDir='{}', page={}, size={}", sortField, sortDir, page, size);

        List<String> camposDeEntidadValidos = Arrays.asList(
                "id", "descripcion", "precio", "marca",
                "categoria.nombre", "proveedor.nombre",
                "unidades", "valoracion", "fechaAlta", "esPerecedero"
        );

        if (!camposDeEntidadValidos.contains(sortField)) {
            logger.warn("[AdminController] CAMPO DE ORDENACIÓN NO VÁLIDO: '{}'. Revirtiendo a 'descripcion'.", sortField);
            model.addAttribute("errorAlOrdenar", "El campo de ordenación '" + sortField + "' no es válido. Se ha ordenado por descripción.");
            sortField = "descripcion";
            sortDir = "asc";
        }

        logger.info("[AdminController] Usando para Pageable: sortField='{}', sortDir='{}'", sortField, sortDir);


        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortField));
        logger.info("[AdminController] Pageable Sort Config: {}", pageable.getSort());


        if (session.getAttribute("emailAutenticadoAdmin") == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para acceder a esta página.");
            return "redirect:/administrador/inicio-sesion";
        }

        String currentSortField = sortField;
        Sort.Direction currentSortDirection = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;

        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        String apiUrl = baseUrl + "/api/administrador/productos";
        String apiUrlCategorias = baseUrl + "/api/administrador/productos/categorias";
        String apiUrlProveedores = baseUrl + "/api/administrador/productos/proveedores";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("sort", currentSortField + "," + (currentSortDirection == Sort.Direction.DESC ? "desc" : "asc"));
        String apiUrlCompleta = builder.toUriString();
        logger.info("[AdminController] URL API completa construida para productos: {}", apiUrlCompleta);
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
        if (criterios.getProveedorId() != null) {
            builder.queryParam("proveedorId", criterios.getProveedorId().toString());
        }
        if (criterios.getEsPerecedero() != null) {
            builder.queryParam("esPerecedero", criterios.getEsPerecedero());
        }

        Page<ProductoResultadoDTO> paginaProductos = null;
        List<CategoriaSimpleDTO> categorias = Collections.emptyList();
        List<ProveedorSimpleDTO> proveedores = Collections.emptyList();
        try {
            // Llamada para obtener productos
            ResponseEntity<RestPage<ProductoResultadoDTO>> responseEntity = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<RestPage<ProductoResultadoDTO>>() {
                    }
            );
            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                paginaProductos = responseEntity.getBody();
            } else {
                paginaProductos = new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0);
                model.addAttribute("errorApi", "No se pudieron cargar los productos desde la API (código: " + responseEntity.getStatusCode() + ")");
            }
            // Llamada para obtener categorías
            ResponseEntity<List<CategoriaSimpleDTO>> responseEntityCategorias = restTemplate.exchange(
                    apiUrlCategorias,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<CategoriaSimpleDTO>>() {
                    }
            );
            if (responseEntityCategorias.getStatusCode().is2xxSuccessful()) {
                categorias = responseEntityCategorias.getBody();
            } else {
                model.addAttribute("errorApiFiltros", "No se pudieron cargar las categorías para los filtros.");
            }

            // Llamada para obtener proveedores
            ResponseEntity<List<ProveedorSimpleDTO>> responseEntityProveedores = restTemplate.exchange(
                    apiUrlProveedores,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ProveedorSimpleDTO>>() {
                    }
            );
            if (responseEntityProveedores.getStatusCode().is2xxSuccessful()) {
                proveedores = responseEntityProveedores.getBody();
            } else {
                model.addAttribute("errorApiFiltros", (model.containsAttribute("errorApiFiltros") ? model.getAttribute("errorApiFiltros") + " " : "") + "No se pudieron cargar los proveedores para los filtros.");
            }
        } catch (Exception e) {
            paginaProductos = new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0);
            model.addAttribute("errorApi", "Error inesperado al obtener productos: " + e.getMessage());
            System.err.println("Error llamando a la API de productos: " + e.getMessage());
            e.printStackTrace();
        }

        model.addAttribute("paginaProductos", paginaProductos);
        model.addAttribute("categorias", categorias);
        model.addAttribute("proveedores", proveedores);
        model.addAttribute("sortField", currentSortField);
        model.addAttribute("sortDir", currentSortDirection == Sort.Direction.DESC ? "desc" : "asc");
        model.addAttribute("reverseSortDir", currentSortDirection == Sort.Direction.ASC ? "desc" : "asc");

        return "consultaProductos";
    }


    /* ------------------------ GESTION DE EMPLEADOS ----------------------------- */
    /* ------------------------ Búsqueda Parametrizada de Empleados ----------------------------- */
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


    @GetMapping("/editar-empleado/{id}")
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

    @PostMapping("/editar-empleado")
    public String procesarEdicionEmpleado(@ModelAttribute("empleado") EmpleadoDTO dto,
                                          RedirectAttributes redirectAttributes) {

        Optional<Empleado> empleadoOpt = empleadoService.buscarPorDni(dto.getNumeroDni());
        if (empleadoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Empleado no encontrado.");
            return "redirect:/administrador/consulta-empleado";
        }

        if(empleadoOpt.isPresent()){
            Empleado empleado = empleadoOpt.get();
            empleado.setNombre(dto.getNombre());
            empleado.setApellidos(dto.getApellido());
            empleado.setEdad(dto.getEdad());
            Departamento departamento = null;
            try {
                departamento = departamentoRepository.findByNombreDept(dto.getNombreDepartamento())
                        .map(obj -> (Departamento) obj)
                        .orElse(null);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            empleado.setDepartamento(departamento);

            empleadoRepository.save(empleado);
        }





        // Guarda los cambios

        redirectAttributes.addFlashAttribute("exito", "Empleado actualizado correctamente.");
        return "redirect:/administrador/consulta-empleado";
    }


}