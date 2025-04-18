package org.grupo_h.empleados.controller;

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


import java.util.Optional;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository; // Inyectar repositorio
    private final BCryptPasswordEncoder passwordEncoder; // Inyectar encoder
    private static final int MAX_INTENTOS_FALLIDOS = 2;

    @Autowired
    public UsuarioController(UsuarioService usuarioService, UsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Muestra el formulario de registro
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuarioRegistroDTO", new UsuarioRegistroDTO());
        return "registro"; // corresponde a registro.html
    }

    // Procesa el envío del formulario de registro
    @PostMapping("/registro")
    public String registrarUsuario(
            @Valid @ModelAttribute("usuarioRegistroDTO") UsuarioRegistroDTO usuarioRegistroDTO,
            BindingResult result,
            Model model) {

        // Si hay errores de validación, se retorna a la misma vista
        if (result.hasErrors()) {
            return "registro";
        }

        try {
            usuarioService.registrarUsuario(usuarioRegistroDTO);
        } catch (RuntimeException ex) {
            // Si ocurre un error (por ejemplo, usuario ya existente), se añade al modelo y se vuelve al formulario
            model.addAttribute("error", ex.getMessage());
            return "registro";
        }

        // Redirige al formulario de registro con un parámetro de éxito si todo va bien
        return "redirect:/usuarios/registro?success";
    }

    // AUTENTICACION

    // --- LOGIN PASO 1: Pedir Usuario ---
    @GetMapping("/inicio-sesion")
    public String mostrarFormularioUsuario(Model model,
                                           @RequestParam(value = "error", required = false) String error,
                                           @RequestParam(value = "logout", required = false) String logout,
                                           // Recibe el flash attribute del logout
                                           @ModelAttribute("logoutReferer") String logoutReferer) {
        if (logout != null) {
            model.addAttribute("mensaje", "Has cerrado sesión exitosamente.");
            // Añadir el referer al modelo si existe para mostrar el enlace
            if (logoutReferer != null && !logoutReferer.isEmpty()) {
                model.addAttribute("showLogoutReferer", true);
                model.addAttribute("logoutRefererUrl", logoutReferer);
                model.addAttribute("logoutRefererText", obtenerUrl(logoutReferer));

            }
        }
        model.addAttribute("pedirUsuario", true); // Indicador para la plantilla
        return "autenticacionPorPasos"; // Plantilla que muestra el formulario de usuario
    }

    // --- LOGIN PASO 1: Procesar Usuario ---
    @PostMapping("/inicio-sesion/usuario")
    public String procesarUsuario(@RequestParam String nombreUsuario,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombreUsuario(nombreUsuario);

        if (usuarioOpt.isEmpty() || !usuarioOpt.get().isHabilitado()) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado o deshabilitado");
            return "redirect:/usuarios/inicio-sesion?error=true";
        }

        if (usuarioOpt.get().isCuentaBloqueada()) {
            redirectAttributes.addFlashAttribute("error", "La cuenta " + nombreUsuario + " está bloqueada.");
            return "redirect:/usuarios/inicio-sesion?error=true";
        }

        // Guardar temporalmente el usuario en sesión para el siguiente paso
        session.setAttribute("usuarioParaLogin", nombreUsuario);

        return "redirect:/usuarios/inicio-sesion/password"; // Redirigir a pedir contraseña
    }

    // --- LOGIN PASO 2: Pedir Contraseña ---
    @GetMapping("/inicio-sesion/password")
    public String mostrarFormularioPassword(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String nombreUsuario = (String) session.getAttribute("usuarioParaLogin");

        if (nombreUsuario == null) {
            // Si no hay usuario en sesión (ej. acceso directo a la URL), volver al paso 1
            redirectAttributes.addFlashAttribute("error", "Por favor, introduce primero tu nombre de usuario.");
            return "redirect:/usuarios/inicio-sesion";
        }

        model.addAttribute("usuario", nombreUsuario);
        model.addAttribute("pedirPassword", true); // Indicador para la plantilla
        return "autenticacionPorPasos"; // Plantilla que muestra el formulario de contraseña
    }


    // --- LOGIN PASO 2: Procesar Contraseña y Autenticar ---
    @PostMapping("/inicio-sesion/autenticar")
    public String autenticarManualConPassword(@RequestParam String contrasena,
                                              HttpServletRequest request, // Necesario para el User-Agent
                                              HttpSession session, // Para obtener/modificar la sesión
                                              RedirectAttributes redirectAttributes) {

        String nombreUsuario = (String) session.getAttribute("usuarioParaLogin");
        if (nombreUsuario == null) {
            redirectAttributes.addFlashAttribute("error", "Error de sesión. Por favor, inicia sesión de nuevo.");
            return "redirect:/usuarios/inicio-sesion";
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombreUsuario(nombreUsuario);

        // Doble verificación (aunque ya se hizo en paso 1, por si acaso)
        if (usuarioOpt.isEmpty()) {
            session.removeAttribute("usuarioParaLogin"); // Limpiar sesión
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/usuarios/inicio-sesion?error=true";
        }

        Usuario usuario = usuarioOpt.get();

        // Verificar estado (habilitado/bloqueado) de nuevo
        if (!usuario.isHabilitado() || usuario.isCuentaBloqueada()) {
            session.removeAttribute("usuarioParaLogin");
            redirectAttributes.addFlashAttribute("error", "La cuenta no está disponible.");
            return "redirect:/usuarios/inicio-sesion/password?error=true"; // Volver a pedir pass con error
        }

        // Verificar contraseña
        if (passwordEncoder.matches(contrasena, usuario.getContrasena())) {
            // Éxito

            // 1. Incrementar contador TOTAL y guardar
            usuario.setSesionesTotales(usuario.getSesionesTotales() + 1);

            // 2. Resetear intentos fallidos
            if (usuario.getIntentosFallidos() > 0) {
                usuario.setIntentosFallidos(0);
            }
            usuarioRepository.save(usuario); // Guardar ambos cambios

            // Limpiar el usuario temporal
            session.removeAttribute("usuarioParaLogin");

            // 3. Establecer atributos de sesión definitivos
            session.setAttribute("usuarioAutenticado", usuario.getNombreUsuario());
            session.setAttribute("contadorConexiones", 1); // CONTADOR DE SESIÓN (se inicializa)
            session.setAttribute("userAgent", request.getHeader("User-Agent"));

            System.out.println("Login manual exitoso para: " + nombreUsuario + ". Total logins: " + usuario.getSesionesTotales());

            return "redirect:/usuarios/info"; // Redirigir a la página de información

        } else {
            // Fallo contraseña
            System.out.println("Login manual fallido (contraseña incorrecta) para: " + nombreUsuario);
            usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);
            if (usuario.getIntentosFallidos() >= MAX_INTENTOS_FALLIDOS) {
                usuario.setCuentaBloqueada(true);
                System.out.println("Cuenta bloqueada para usuario: " + nombreUsuario);
                usuarioRepository.save(usuario);
                redirectAttributes.addFlashAttribute("error", "Cuenta bloqueada.");
                return "redirect:/usuarios/inicio-sesion/password?error=true";
            }
            usuarioRepository.save(usuario); // Guardar intentos/bloqueo

            redirectAttributes.addFlashAttribute("error", "Contraseña incorrecta.");
            // No limpiar 'usuarioParaLogin' para que pueda reintentar la contraseña
            return "redirect:/usuarios/inicio-sesion/password?error=true"; // Volver a pedir pass con error
        }
    }


    // --- ENDPOINT 3: Mostrar Información ---
    @GetMapping("/info")
    public String mostrarInformacionUsuario(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String nombreUsuario = (String) session.getAttribute("usuarioAutenticado");

        // Proteger el endpoint: verificar si el usuario está autenticado manualmente
        if (nombreUsuario == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para ver esta información.");
            return "redirect:/usuarios/inicio-sesion";
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombreUsuario(nombreUsuario);
        if (usuarioOpt.isEmpty()) {
            // Raro que ocurra si está en sesión, pero por seguridad
            session.invalidate(); // Invalidar sesión corrupta
            redirectAttributes.addFlashAttribute("error", "Error al recuperar datos de usuario.");
            return "redirect:/usuarios/inicio-sesion";
        }

        Usuario usuario = usuarioOpt.get();
        Integer contadorSesion = (Integer) session.getAttribute("contadorConexiones");
        String userAgent = (String) session.getAttribute("userAgent");

        // Añadir datos al modelo para la vista
        model.addAttribute("nombreUsuario", nombreUsuario);
        model.addAttribute("totalLoginsUsuario", usuario.getSesionesTotales()); // Contador persistente
        model.addAttribute("loginsSesionActual", contadorSesion != null ? contadorSesion : 0); // Contador de sesión
        model.addAttribute("navegadorActual", userAgent != null ? userAgent : "No disponible");

        // Aquí no mostramos el enlace de logout referer, se muestra en la pág de login

        return "infoUsuario"; // Nombre de la nueva plantilla HTML
    }


    // --- LOGOUT MANUAL (Modificado para Referer) ---
    @GetMapping("/logout")
    public String logoutManual(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        HttpSession session = request.getSession(false);
        String referer = request.getHeader("Referer"); // Capturar página anterior

        if (session != null) {
            System.out.println("Invalidando sesión HTTP: " + session.getId());
            session.invalidate();
        }

        // Añadir el referer como flash attribute para que sobreviva la redirección
        if (referer != null && !referer.contains("/inicio-sesion")) { // Evitar referer desde páginas de login
            redirectAttributes.addFlashAttribute("logoutReferer", referer);
        }

        System.out.println("Logout manual ejecutado. Redirigiendo a /usuarios/inicio-sesion?logout=true");
        return "redirect:/usuarios/inicio-sesion?logout=true";
    }


    // --- RECUPERACIÓN DE CONTRASEÑA (Adaptado, ¡PERO INSEGURO!) ---
    @GetMapping("/recuperar-contraseña")
    @ResponseBody
    public ResponseEntity<String> recuperarContraseña(
            @RequestParam(value = "usuario", required = false) String usuario) {

        if (usuario == null || usuario.isEmpty()) {
            return ResponseEntity.badRequest().body("Falta el parámetro 'usuario'");
        }

        Optional<Usuario> userOpt = usuarioRepository.findByNombreUsuario(usuario);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        // Respuesta temporal segura (indica que se iniciaría el proceso)
        return ResponseEntity.ok(usuarioRepository.findByNombreUsuario(usuario).get().getContrasena());
    }

    // Método auxiliar simple para texto del referer
    private String obtenerUrl(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }
        return url; // Por defecto, mostrar la URL
    }
}
