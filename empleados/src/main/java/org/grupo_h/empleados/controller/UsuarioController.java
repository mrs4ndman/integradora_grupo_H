package org.grupo_h.empleados.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.grupo_h.comun.entity.Usuario;
import org.grupo_h.empleados.dto.UsuarioRegistroDTO;
import org.grupo_h.empleados.repository.UsuarioRepository;
import org.grupo_h.empleados.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

/**
 * Controlador para gestionar las operaciones relacionadas con los usuarios.
 */
@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private static final int MAX_INTENTOS_FALLIDOS = 3;

    @Autowired
    public UsuarioController(UsuarioService usuarioService, UsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
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
        } catch (RuntimeException ex) {
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
                    // Decodificar si es necesario (ej. Base64) y separar por comas
                    String decodedValue = new String(java.util.Base64.getUrlDecoder().decode(cookie.getValue()));
                    previousLogins = new ArrayList<>(Arrays.asList(decodedValue.split(",")));
                    break;
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
     * @param email      Email de usuario introducido.
     * @param session            Sesión HTTP.
     * @param redirectAttributes Atributos para redirección.
     * @return Redirección al formulario de contraseña o de error.
     */
    @PostMapping("/inicio-sesion/usuario")
    public String procesarUsuario(@RequestParam String email,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty() || !usuarioOpt.get().isHabilitado()) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado o deshabilitado");
            return "redirect:/usuarios/inicio-sesion?error=true";
        }

        if (usuarioOpt.get().isCuentaBloqueada()) {
            redirectAttributes.addFlashAttribute("error", "La cuenta " + email + " está bloqueada.");
            return "redirect:/usuarios/inicio-sesion?error=true";
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

        if (!usuario.isHabilitado() || usuario.isCuentaBloqueada()) {
            session.removeAttribute("emailParaLogin");
            redirectAttributes.addFlashAttribute("error", "La cuenta no está disponible.");
            return "redirect:/usuarios/inicio-sesion/password?error=true";
        }

        if (passwordEncoder.matches(contrasena, usuario.getContrasena())) {
            // --- Inicio: Lógica de Cookies ---
            Set<String> logins = new LinkedHashSet<>(); // Usar Set para evitar duplicados
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
            logins.add(email); // Añadir el email actual

            // Limitar el número de usuarios recordados si se desea (opcional)
            // final int MAX_USERS = 5;
            // if (logins.size() > MAX_USERS) {
            //    logins = new LinkedHashSet<>(new ArrayList<>(logins).subList(logins.size() - MAX_USERS, logins.size()));
            // }

            String joinedLogins = String.join(",", logins);
            String encodedValue = Base64.getUrlEncoder().withoutPadding().encodeToString(joinedLogins.getBytes());

            Cookie loginCookie = new Cookie("loginsAnteriores", encodedValue);
            loginCookie.setPath("/"); // Asegurar que la cookie esté disponible en todo el sitio
            loginCookie.setMaxAge(60 * 60 * 24 * 30); // Ejemplo: 30 días de duración
            loginCookie.setHttpOnly(true); // Por seguridad
            // loginCookie.setSecure(true); // Descomentar si usas HTTPS
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

            return "redirect:/usuarios/info";
        } else {
            usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);
            if (usuario.getIntentosFallidos() >= MAX_INTENTOS_FALLIDOS) {
                usuario.setCuentaBloqueada(true);
                usuarioRepository.save(usuario);
                redirectAttributes.addFlashAttribute("error", "Cuenta bloqueada.");
                return "redirect:/usuarios/inicio-sesion/password?error=true";
            }
            usuarioRepository.save(usuario);

            redirectAttributes.addFlashAttribute("error", "Contraseña incorrecta.");
            return "redirect:/usuarios/inicio-sesion/password?error=true";
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
    public String logoutManual(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        HttpSession session = request.getSession(false);
        String referer = request.getHeader("Referer");

        if (session != null) {
            session.invalidate();
        }

        if (referer != null && !referer.contains("/inicio-sesion")) {
            redirectAttributes.addFlashAttribute("logoutReferer", referer);
        }

        return "redirect:/usuarios/inicio-sesion?logout=true";
    }

    /**
     * Recupera la contraseña de un usuario (¡Inseguro!).
     *
     * @param email Email del usuario.
     * @return Respuesta con la contraseña o mensaje de error.
     */
    @GetMapping("/recuperar-contraseña")
    @ResponseBody
    public ResponseEntity<String> recuperarContrasenia(
            @RequestParam(value = "email", required = false) String email) {

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Falta el parámetro 'usuario'");
        }

        Optional<Usuario> userOpt = usuarioRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        return ResponseEntity.ok(usuarioRepository.findByEmail(email).get().getContrasena());
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
