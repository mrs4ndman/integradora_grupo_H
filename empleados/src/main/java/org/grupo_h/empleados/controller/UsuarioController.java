package org.grupo_h.empleados.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.grupo_h.comun.entity.Usuario;
import org.grupo_h.empleados.component.HistorialSesionUsuario;
import org.grupo_h.empleados.dto.PaginaVisitada;
import org.grupo_h.empleados.dto.ReseteoContrasenaDTO;
import org.grupo_h.empleados.dto.UsuarioRegistroDTO;
import org.grupo_h.comun.repository.UsuarioRepository;
import org.grupo_h.empleados.service.ParametrosService;
import org.grupo_h.empleados.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador para gestionar las operaciones relacionadas con los usuarios.
 */
@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final ParametrosService parametrosService;
    private final BCryptPasswordEncoder passwordEncoder;
    private static final int MAX_INTENTOS_FALLIDOS = 3;
    @Autowired
    private HistorialSesionUsuario historialSesionUsuario;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UsuarioController(UsuarioService usuarioService, UsuarioRepository usuarioRepository, ParametrosService parametrosService, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.parametrosService = parametrosService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Muestra el formulario de registro de usuario.
     *
     * @param model Modelo para pasar datos a la vista.
     * @return Nombre de la vista de registro.
     */
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuarioRegistroDTO", new UsuarioRegistroDTO());
        return "registro";
    }

    /**
     * Procesa el formulario de registro de usuario.
     *
     * @param usuarioRegistroDTO DTO con los datos del formulario.
     * @param result             Resultado de la validación.
     * @param model              Modelo para pasar datos a la vista.
     * @return Redirección a la vista de registro con parámetro de éxito o error.
     */
    @PostMapping("/registro")
    public String registrarUsuario(
            @Valid @ModelAttribute("usuarioRegistroDTO") UsuarioRegistroDTO usuarioRegistroDTO,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "registro";
        }

        try {
            usuarioService.registrarUsuario(usuarioRegistroDTO);
        } catch (Exception ex) {
            result.reject("registroError", "No se pudo completar el registro: " + ex.getMessage());
            model.addAttribute("error", ex.getMessage());
            return "registro";
        }

        return "redirect:/usuarios/registro?success";
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
    public String mostrarFormularioUsuario(Model model,
                                           HttpServletRequest request,
                                           @RequestParam(value = "error", required = false) String error,
                                           @RequestParam(value = "logout", required = false) String logout,
                                           @ModelAttribute("logoutReferer") String logoutReferer) {
        List<String> previousLogins = new ArrayList<>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("loginsAnteriores".equals(cookie.getName())) {
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
                            System.err.println("Error al decodificar cookie loginsAnteriores: " + e.getMessage());
                            // Dejar previousLogins vacía
                        }
                    }
                    break; // Encontrada la cookie, salir del bucle
                }
            }
        }
        model.addAttribute("loginsAnteriores", previousLogins);

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
     * Procesa el email de usuario para el inicio de sesión.
     *
     * @param email              Email de usuario introducido.
     * @param session            Sesión HTTP.
     * @param redirectAttributes Atributos para redirección.
     * @return Redirección al formulario de contraseña o de error.
     */
    @PostMapping("/inicio-sesion/usuario")
    public String procesarUsuario(@RequestParam String email,
                                  HttpServletRequest request,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {

        // --- Comprobación Remember Me ---
        Cookie[] cookies = request.getCookies();
        String rememberMeTokenValue = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("remember-me-token".equals(cookie.getName())) {
                    rememberMeTokenValue = cookie.getValue();
                    break;
                }
            }
        }

        if (rememberMeTokenValue != null && !rememberMeTokenValue.isEmpty()) {
            System.out.println("Encontrada cookie remember-me: " + rememberMeTokenValue);
            Optional<Usuario> usuarioOptByToken = usuarioRepository.findByRememberMeToken(rememberMeTokenValue);

            if (usuarioOptByToken.isPresent()) {
                Usuario usuarioRecordado = usuarioOptByToken.get();
                System.out.println("Usuario encontrado por token: " + usuarioRecordado.getEmail());
                if (usuarioRecordado.getEmail().equalsIgnoreCase(email) &&
                        usuarioRecordado.getRememberMeTokenExpiry() != null &&
                        usuarioRecordado.getRememberMeTokenExpiry().isAfter(LocalDateTime.now()) &&
                        usuarioRecordado.isHabilitado() && !usuarioRecordado.isCuentaBloqueada() ) {

                    System.out.println("Token válido para " + email + ". Re-autenticando.");
                    session.setAttribute("emailAutenticado", usuarioRecordado.getEmail());
                    Integer contadorActual = (Integer) session.getAttribute("contadorConexiones");
                    int nuevoContador = (contadorActual == null) ? 1 : contadorActual + 1;
                    session.setAttribute("contadorConexiones", nuevoContador);
                    session.setAttribute("userAgent", request.getHeader("User-Agent"));
                     usuarioRecordado.setSesionesTotales(usuarioRecordado.getSesionesTotales() + 1);
                     usuarioRepository.save(usuarioRecordado);
                    redirectAttributes.addFlashAttribute("contadorConexionesFlash", nuevoContador);

                    return "redirect:/usuarios/info";
                } else {
                    System.out.println("Token inválido (expirado, email no coincide, cuenta no activa) para: " + email);
                    // Token inválido, expirado, email no coincide o cuenta bloqueada/deshabilitada.
                    if(usuarioRecordado.getRememberMeToken() != null){
                        usuarioRecordado.setRememberMeToken(null);
                        usuarioRecordado.setRememberMeTokenExpiry(null);
                        usuarioRepository.save(usuarioRecordado);
                    }
                    Cookie removeCookie = new Cookie("remember-me-token", "");
                    removeCookie.setPath("/");
                    removeCookie.setMaxAge(0);
                }
            } else {
                System.out.println("Token de cookie no encontrado en BD.");
                Cookie removeCookie = new Cookie("remember-me-token", "");
                removeCookie.setPath("/");
                removeCookie.setMaxAge(0);
            }
        }
        // --- Fin Comprobación Remember Me ---

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()){
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/usuarios/inicio-sesion?error=true";
        }

        Usuario usuario = usuarioOpt.get();

        if (!usuarioOpt.get().isHabilitado()) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado o deshabilitado");
            return "redirect:/usuarios/inicio-sesion?error=true";
        }

        if (usuario.isCuentaBloqueada()) {
            LocalDateTime horaDesbloqueo = usuario.getTiempoHastaDesbloqueo();
            if (horaDesbloqueo != null && LocalDateTime.now().isBefore(horaDesbloqueo)) {
                // Todavía bloqueada: Mostrar mensaje específico
                DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                        .withLocale(Locale.of("es", "ES"));
                String unlockTimeString = horaDesbloqueo.format(formatter);
                String mensajeError = "Su cuenta está bloqueada temporalmente. Podrá intentar de nuevo después de las " + unlockTimeString + " - Motivo: " + usuario.getMotivoBloqueo();
                redirectAttributes.addFlashAttribute("error", mensajeError);
                return "redirect:/usuarios/inicio-sesion?error=true";
            } else {
                usuarioService.desbloquearCuenta(email);
            }
        }


        session.setAttribute("emailParaLogin", email);
        return "redirect:/usuarios/inicio-sesion/password";
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
        String email = (String) session.getAttribute("emailParaLogin");

        if (email == null) {
            redirectAttributes.addFlashAttribute("error", "Por favor, introduce primero tu email.");
            return "redirect:/usuarios/inicio-sesion";
        }

        model.addAttribute("email", email);
        model.addAttribute("pedirPassword", true);
        return "autenticacionPorPasos";
    }

    /**
     * Procesa la contraseña y autentica al usuario.
     *
     * @param contrasena        Contraseña introducida.
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
        String email = (String) session.getAttribute("emailParaLogin");

        if (email == null) {
            redirectAttributes.addFlashAttribute("error", "Error de sesión. Por favor, inicia sesión de nuevo.");
            return "redirect:/usuarios/inicio-sesion";
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            session.removeAttribute("emailParaLogin");
            redirectAttributes.addFlashAttribute("error", "Email no encontrado.");
            return "redirect:/usuarios/inicio-sesion?error=true";
        }

        Usuario usuario = usuarioOpt.get();

        boolean debeRedirigirPorErrorPrevio = false;
        String mensajeErrorPrevio = null;

        if (!usuario.isHabilitado()) {
            mensajeErrorPrevio = "La cuenta no está disponible (deshabilitada).";
            debeRedirigirPorErrorPrevio = true;
        } else if (usuario.isCuentaBloqueada()) {
            LocalDateTime horaDesbloqueo = usuario.getTiempoHastaDesbloqueo();
            if (horaDesbloqueo != null && LocalDateTime.now().isBefore(horaDesbloqueo)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                        .withLocale(Locale.of("es", "ES"));
                String unlockTimeString = horaDesbloqueo.format(formatter);
                mensajeErrorPrevio = "Su cuenta está bloqueada temporalmente. Podrá intentar de nuevo después de las " + unlockTimeString + " - Motivo: " + usuario.getMotivoBloqueo();
                debeRedirigirPorErrorPrevio = true;
            } else {
                usuarioService.desbloquearCuenta(email);
                usuario = usuarioRepository.findByEmail(email).orElse(usuario);
            }
        }

        if (debeRedirigirPorErrorPrevio) {
            session.removeAttribute("emailParaLogin");
            redirectAttributes.addFlashAttribute("error", mensajeErrorPrevio);
            return "redirect:/usuarios/inicio-sesion?error=true";
        }

        if (passwordEncoder.matches(contrasena, usuario.getContrasena())) {
            // --- Inicio: Lógica de Cookies ---
            Set<String> logins = new LinkedHashSet<>();
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("loginsAnteriores".equals(cookie.getName())) {
                        String decodedValue = new String(Base64.getUrlDecoder().decode(cookie.getValue()));
                        logins.addAll(Arrays.asList(decodedValue.split(",")));
                        break;
                    }
                }
            }
            logins.add(email);

            String joinedLogins = String.join(",", logins);
            String encodedValue = Base64.getUrlEncoder().withoutPadding().encodeToString(joinedLogins.getBytes());

            Cookie loginCookie = new Cookie("loginsAnteriores", encodedValue);
            loginCookie.setPath("/");
            loginCookie.setMaxAge(60 * 60 * 24 * 30); //30 días de duración
            loginCookie.setHttpOnly(true);
            response.addCookie(loginCookie);
            // --- Fin: Lógica de Cookies ---

            usuario.setSesionesTotales(usuario.getSesionesTotales() + 1);
            if (usuario.getIntentosFallidos() > 0) {
                usuario.setIntentosFallidos(0);
            }
            usuarioRepository.save(usuario);

            session.removeAttribute("emailParaLogin");

            Integer contadorActual = (Integer) session.getAttribute("contadorConexiones");
            int nuevoContador = (contadorActual == null) ? 1 : contadorActual + 1;
            session.setAttribute("contadorConexiones", nuevoContador);

            redirectAttributes.addFlashAttribute("contadorConexionesFlash", nuevoContador);

            session.setAttribute("emailAutenticado", usuario.getEmail());
            session.setAttribute("contadorConexiones", 1);
            session.setAttribute("userAgent", request.getHeader("User-Agent"));

            // --- Lógica Remember Me ---
            try {
                String rememberMeTokenValue = UUID.randomUUID().toString();
                // Duración de la cookie/token (14 días)
                LocalDateTime rememberMeExpiry = LocalDateTime.now().plusDays(14);

                usuario.setRememberMeToken(rememberMeTokenValue);
                usuario.setRememberMeTokenExpiry(rememberMeExpiry);
                usuarioRepository.save(usuario); // Guardar token en BD

                Cookie rememberMeCookie = new Cookie("remember-me-token", rememberMeTokenValue);
                rememberMeCookie.setPath("/");
                rememberMeCookie.setMaxAge(14 * 24 * 60 * 60); // 14 días
                rememberMeCookie.setHttpOnly(true);
                response.addCookie(rememberMeCookie);
                System.out.println("Cookie remember-me creada para: " + email);

            } catch (Exception e) {
                System.err.println("Error al crear cookie remember-me: " + e.getMessage());
            }
            // --- Fin Lógica Remember Me ---

            return "redirect:/usuarios/info";
        } else {
            // Llama al servicio para manejar el fallo
            usuarioService.procesarLoginFallido(email);
            // Comprueba si la cuenta está ahora bloqueada para el mensaje de error
            Usuario usuarioActualizado = usuarioRepository.findByEmail(email).orElse(usuario); // Recarga para obtener estado actualizado
            String mensajeErrorFallido;
            int maxIntentos = parametrosService.getMaxIntentosFallidos();
            if (usuarioActualizado.isCuentaBloqueada()) {
                // ¡LA CUENTA SE ACABA DE BLOQUEAR!
                LocalDateTime horaDesbloqueo = usuarioActualizado.getTiempoHastaDesbloqueo();
                if (horaDesbloqueo != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                            .withLocale(Locale.of("es", "ES"));
                    String unlockTimeString = horaDesbloqueo.format(formatter);
                    mensajeErrorFallido = "Contraseña incorrecta. Cuenta bloqueada. Podrá intentar de nuevo después de las " + unlockTimeString;
                } else {
                    mensajeErrorFallido = "Contraseña incorrecta. Cuenta bloqueada."; // Fallback
                }

                redirectAttributes.addFlashAttribute("error", mensajeErrorFallido);
                session.removeAttribute("emailParaLogin"); // Quitar email de la sesión
                // Redirigir a la página de EMAIL (inicio-sesion)
                return "redirect:/usuarios/inicio-sesion?error=true";

            } else {
                // LA CUENTA NO SE BLOQUEÓ (aún quedan intentos)
                mensajeErrorFallido = "Contraseña incorrecta. Intentos restantes: " + (maxIntentos - usuarioActualizado.getIntentosFallidos());
                redirectAttributes.addFlashAttribute("error", mensajeErrorFallido);
                // Redirigir a la página de CONTRASEÑA (como antes)
                return "redirect:/usuarios/inicio-sesion/password?error=true";
            }
        }
    }

    @PostMapping("/inicio-sesion/eliminar-usuario")
    public ResponseEntity<?> eliminarUsuarioCookie(@RequestParam String emailAEliminar,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) {
        Set<String> logins = new LinkedHashSet<>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("loginsAnteriores".equals(cookie.getName())) {
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

            Cookie loginCookie = new Cookie("loginsAnteriores", encodedValue);
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
     * Muestra la información del usuario autenticado.
     *
     * @param model              Modelo para pasar datos a la vista.
     * @param session            Sesión HTTP.
     * @param redirectAttributes Atributos para redirección.
     * @return Nombre de la vista de información del usuario.
     */
    @GetMapping("/info")
    public String mostrarInformacionUsuario(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String email = (String) session.getAttribute("emailAutenticado");

        Integer contadorSesion = null;

        if (model.containsAttribute("contadorConexionesFlash")) {
            try {
                contadorSesion = (Integer) model.getAttribute("contadorConexionesFlash");
                if (contadorSesion != null) {
                    session.setAttribute("contadorConexiones", contadorSesion);
                }
            } catch (Exception e) {
                Object contadorObj = session.getAttribute("contadorConexiones");
                if (contadorObj instanceof Integer) {
                    contadorSesion = (Integer) contadorObj;
                }
            }
        } else {
            Object contadorObj = session.getAttribute("contadorConexiones");
            if (contadorObj instanceof Integer) {
                contadorSesion = (Integer) contadorObj;
            }
        }

        if (email == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para ver esta información.");
            return "redirect:/usuarios/inicio-sesion";
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            session.invalidate();
            redirectAttributes.addFlashAttribute("error", "Error al recuperar datos de usuario.");
            return "redirect:/usuarios/inicio-sesion";
        }

        Usuario usuario = usuarioOpt.get();
        String userAgent = (String) session.getAttribute("userAgent");

        model.addAttribute("email", email);
        model.addAttribute("totalLoginsUsuario", usuario.getSesionesTotales());
        model.addAttribute("loginsSesionActual", contadorSesion != null ? contadorSesion : 0);
        model.addAttribute("navegadorActual", userAgent != null ? userAgent : "No disponible");

        return "areaPersonal";
    }

    /**
     * Cierra la sesión del usuario.
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
        String email = (session != null) ? (String) session.getAttribute("emailAutenticado") : null;
        if (email != null) {
            Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                usuario.setRememberMeToken(null);
                usuario.setRememberMeTokenExpiry(null);
                usuarioRepository.save(usuario);
                System.out.println("Token remember-me limpiado para: " + email);
            }
        }
        // Borrar la cookie
        Cookie removeCookie = new Cookie("remember-me-token", "");
        removeCookie.setPath("/");
        removeCookie.setMaxAge(0);
        response.addCookie(removeCookie);
        // --- Fin Limpiar Remember Me ---

        if (historialSesionUsuario != null) {
            List<PaginaVisitada> pages = historialSesionUsuario.getVisitedPages();

            historialSesionUsuario.clearHistory();
            logger.info("logoutManual: Se ha limpiado el historial de paginas visitadas.");
            List<PaginaVisitada> pagesAfterClear = historialSesionUsuario.getVisitedPages();
        }

        if (session != null) {
            session.invalidate();
        }

        if (referer != null && !referer.contains("/inicio-sesion")) {
            redirectAttributes.addFlashAttribute("logoutReferer", referer);
        }

        return "redirect:/usuarios/inicio-sesion?logout=true";
    }

    @GetMapping("/cambiar-password")
    public String mostrarFormularioCambioDirecto(HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        String emailEnSesion = (String) session.getAttribute("emailParaLogin");

        // Verifica si el usuario fue identificado previamente en el intento de login
        if (emailEnSesion == null || emailEnSesion.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Proceso inválido. Por favor, empieza de nuevo desde el inicio de sesión.");
            return "redirect:/usuarios/inicio-sesion";
        }

        if (!model.containsAttribute("reseteoContraseñaData")) {
            model.addAttribute("reseteoContraseñaData", new ReseteoContrasenaDTO());
        }

        model.addAttribute("resetearPassword", true);
        model.addAttribute("emailParaLogin", emailEnSesion);
        return "autenticacionPorPasos";
    }

    @PostMapping("/cambiar-password")
    public String procesarCambioDirecto(
            @Valid @ModelAttribute("reseteoContraseñaData") ReseteoContrasenaDTO reseteoContraseñaData,
            BindingResult result,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String emailEnSesion = (String) session.getAttribute("emailParaLogin");

        if (emailEnSesion == null || emailEnSesion.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Error inesperado. Por favor, inicia sesión de nuevo.");
            session.removeAttribute("emailParaLogin");
            return "redirect:/usuarios/inicio-sesion";
        }

//        session.removeAttribute("emailParaLogin");

        // 3. VERIFICAR RESULTADOS DE VALIDACIÓN del DTO
        if (result.hasErrors()) {
            // Guardar los errores y el DTO en Flash para mostrarlos en la vista
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.reseteoContraseñaData", result);
            redirectAttributes.addFlashAttribute("reseteoContraseñaData", reseteoContraseñaData);
//            redirectAttributes.addFlashAttribute("error", "La contraseña introducida no cumple los requisitos.");
            return "redirect:/usuarios/cambiar-password";
        }

        // 4. Si la validación es OK, proceder a actualizar
        // Obtener la contraseña validada del DTO
        String nuevaPasswordValidada = reseteoContraseñaData.getNuevaContraseña();

        boolean actualizado = usuarioService.actualizarPassword(emailEnSesion, nuevaPasswordValidada);

        if (actualizado) {
            redirectAttributes.addFlashAttribute("mensaje", "Contraseña actualizada correctamente. Ya puedes iniciar sesión.");
        } else {
            redirectAttributes.addFlashAttribute("error", "No se pudo actualizar la contraseña. Inténtalo de nuevo.");
        }
        return "redirect:/usuarios/inicio-sesion";
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
}
