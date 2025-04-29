package org.grupo_h.administracion.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.grupo_h.administracion.dto.EmpleadoDetalleDTO;
import org.grupo_h.administracion.dto.UsuariosForm;
import org.grupo_h.administracion.service.EmpleadoService;
import org.grupo_h.administracion.service.UsuarioService;
import org.grupo_h.comun.entity.Administrador;
import org.grupo_h.administracion.service.ParametrosService;
import org.grupo_h.administracion.service.AdministradorService;
import org.grupo_h.comun.entity.Usuario;
import org.grupo_h.comun.repository.AdministradorRepository;
import org.grupo_h.comun.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @Autowired
    public AdministradorController(AdministradorService administradorService,
                                   AdministradorRepository administradorRepository,
                                   EmpleadoService empleadoService,
                                   ParametrosService parametrosService,
                                   BCryptPasswordEncoder passwordEncoder,
                                   UsuarioRepository usuarioRepository,
                                   UsuarioService usuarioService) {
        this.administradorService = administradorService;
        this.administradorRepository = administradorRepository;
        this.empleadoService = empleadoService;
        this.parametrosService = parametrosService;
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
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
        List<String> previousLogins = new ArrayList<>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("loginsAnterioresAdmin".equals(cookie.getName())) {
                    String cookieValue = cookie.getValue();
                    // Comprobar que el valor de la cookie no sea nulo ni esté vacío antes de procesar
                    if (cookieValue != null && !cookieValue.isEmpty()) {
                        try {
                            String decodedValue = new String(Base64.getUrlDecoder().decode(cookieValue));
                            // Dividir Y FILTRAR cadenas vacías o nulas
                            previousLogins = Arrays.stream(decodedValue.split(","))
                                    .map(String::trim) // Opcional: quitar espacios blancos alrededor
                                    .filter(email -> email != null && !email.isEmpty()) // <-- Filtrar vacíos
                                    .collect(Collectors.toList()); // Recolectar en la lista
                        } catch (IllegalArgumentException e) {
                            // Manejar posible error en Base64 si la cookie está corrupta
                            System.err.println("Error al decodificar cookie loginsAnterioresAdmin: " + e.getMessage());
                            // Dejar previousLogins vacía
                        }
                    }
                    break; // Encontrada la cookie, salir del bucle
                }
            }
        }
        model.addAttribute("loginsAnterioresAdmin", previousLogins);

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
            Optional<Administrador> administradorOptByToken = administradorRepository.findByRememberMeToken(rememberMeTokenValue);

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

        Optional<Administrador> administradorOpt = administradorRepository.findByEmail(email);

        if (administradorOpt.isEmpty() || !administradorOpt.get().isHabilitado()) {
            redirectAttributes.addFlashAttribute("error", "Administrador no encontrado o deshabilitado");
            return "redirect:/administrador/inicio-sesion?error=true";
        }

        Administrador administrador = administradorOpt.get();

        if (administrador.isCuentaBloqueada()) {
            LocalDateTime horaDesbloqueo = administrador.getTiempoHastaDesbloqueo();
            if (horaDesbloqueo != null && LocalDateTime.now().isBefore(horaDesbloqueo)) {
                // Todavía bloqueada: Mostrar mensaje específico
                DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                        .withLocale(new Locale("es", "ES"));
                String unlockTimeString = horaDesbloqueo.format(formatter);
                String mensajeError = "Su cuenta está bloqueada temporalmente. Podrá intentar de nuevo después de las " + unlockTimeString;
                redirectAttributes.addFlashAttribute("error", mensajeError);
                return "redirect:/administrador/inicio-sesion?error=true";
            } else {
                administradorService.desbloquearCuenta(email);
            }
        }

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
            redirectAttributes.addFlashAttribute("error", "Error de sesión. Por favor, inicia sesión de nuevo.");
            return "redirect:/administrador/inicio-sesion";
        }

        Optional<Administrador> administradorOpt = administradorRepository.findByEmail(email);

        if (administradorOpt.isEmpty()) {
            session.removeAttribute("emailParaLoginAdmin");
            redirectAttributes.addFlashAttribute("error", "Email no encontrado.");
            return "redirect:/administrador/inicio-sesion?error=true";
        }

        Administrador administrador = administradorOpt.get();

        boolean debeRedirigirPorErrorPrevio = false;
        String mensajeErrorPrevio = null;
        if (!administrador.isHabilitado()) {
            mensajeErrorPrevio = "La cuenta no está disponible (deshabilitada).";
            debeRedirigirPorErrorPrevio = true;
        } else if (administrador.isCuentaBloqueada()) {
            LocalDateTime horaDesbloqueo = administrador.getTiempoHastaDesbloqueo();
            if (horaDesbloqueo != null && LocalDateTime.now().isBefore(horaDesbloqueo)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                        .withLocale(new Locale("es", "ES"));
                String unlockTimeString = horaDesbloqueo.format(formatter);
                mensajeErrorPrevio = "Su cuenta está bloqueada temporalmente. Podrá intentar de nuevo después de las " + unlockTimeString;
                debeRedirigirPorErrorPrevio = true;
            } else {
                administradorService.desbloquearCuenta(email);
                administrador = administradorRepository.findByEmail(email).orElse(administrador);
            }
        }

        if (debeRedirigirPorErrorPrevio) {
            session.removeAttribute("emailParaLoginAdmin");
            redirectAttributes.addFlashAttribute("error", mensajeErrorPrevio);
            return "redirect:/administrador/inicio-sesion?error=true";
        }

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
                            .withLocale(new Locale("es", "ES"));
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
    @GetMapping("/desbloqueo-usuarios")
    public String desbloqueoUsuariosGet(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String email = (String) session.getAttribute("emailAutenticadoAdmin");

        if (email == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para acceder a esta funcionalidad.");
            return "redirect:/administrador/inicio-sesion";
        }

        List<Usuario> usuariosActuales = usuarioService.obtenerTodosUsuarios();
        System.out.println(usuariosActuales);
        UsuariosForm form = new UsuariosForm();
        form.setUsuariosActuales(usuariosActuales);
        model.addAttribute("usuariosForm", form);

        return "desbloqueoUsuarios";
    }

    @PostMapping("/guardarCambios")
    public String guardarCambios(
            @ModelAttribute("usuariosForm") UsuariosForm usuariosForm) {
        // Extraemos la lista del wrapper
        List<Usuario> lista = usuariosForm.getUsuariosActuales();
        // Llamamos al servicio con la lista real
        usuarioService.actualizarUsuarios(lista);
        return "redirect:/administrador/desbloqueo-usuarios";
    }

}