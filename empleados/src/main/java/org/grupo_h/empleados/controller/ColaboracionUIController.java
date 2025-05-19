package org.grupo_h.empleados.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

/**
 * Controlador MVC para servir las interfaces de usuario (HTML)
 * relacionadas con las colaboraciones y el chat.
 * La autenticación se verifica obteniendo "emailAutenticado" de la HttpSession.
 */
@Controller
@RequestMapping("/empleados/colaboraciones")
public class ColaboracionUIController {

    private static final Logger logger = LoggerFactory.getLogger(ColaboracionUIController.class);

    /**
     * Muestra la página para gestionar las solicitudes de colaboración (emitidas y recibidas)
     * y para crear nuevas solicitudes, permitiendo la búsqueda de empleados.
     *
     * @param model   El modelo para pasar datos a la vista.
     * @param session La sesión HTTP actual.
     * @param redirectAttributes Atributos para mensajes en caso de redirección.
     * @return El nombre de la vista Thymeleaf ("colaboraciones/solicitudes") o una redirección al login.
     */
    @GetMapping("/solicitudes-ui")
    public String mostrarPaginaSolicitudes(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String emailAutenticado = (String) session.getAttribute("emailAutenticado");

        if (emailAutenticado == null || emailAutenticado.isEmpty()) {
            logger.warn("Acceso no autenticado a /empleados/colaboraciones/solicitudes-ui. Redirigiendo al login.");
            redirectAttributes.addFlashAttribute("error", "Debe iniciar sesión para acceder a esta página.");
            return "redirect:/usuarios/inicio-sesion";
        }

        logger.info("Usuario '{}' accediendo a la UI de solicitudes de colaboración.", emailAutenticado);
        model.addAttribute("currentUserEmail", emailAutenticado);
        return "colaboraciones/solicitudes";
    }

    /**
     * Muestra la página de chat para las colaboraciones activas.
     *
     * @param model   El modelo para pasar datos a la vista.
     * @param session La sesión HTTP actual.
     * @param redirectAttributes Atributos para mensajes en caso de redirección.
     * @param colaboracionId (Opcional) Si se proporciona, se intentará abrir este chat directamente.
     * @return El nombre de la vista Thymeleaf ("colaboraciones/chat") o una redirección al login.
     */
    @GetMapping("/chat-ui")
    public String mostrarPaginaChat(Model model, HttpSession session, RedirectAttributes redirectAttributes,
                                    @RequestParam(name = "colaboracionId", required = false) String colaboracionId) {
        String emailAutenticado = (String) session.getAttribute("emailAutenticado");

        if (emailAutenticado == null || emailAutenticado.isEmpty()) {
            logger.warn("Acceso no autenticado a /empleados/colaboraciones/chat-ui. Redirigiendo al login.");
            redirectAttributes.addFlashAttribute("error", "Debe iniciar sesión para acceder a esta página.");
            return "redirect:/usuarios/inicio-sesion";
        }

        logger.info("Usuario '{}' accediendo a la UI de chat. Colaboración preseleccionada ID: {}", emailAutenticado, colaboracionId);
        model.addAttribute("currentUserEmail", emailAutenticado);
        if (colaboracionId != null && !colaboracionId.isEmpty()) {
            model.addAttribute("selectedColaboracionId", colaboracionId);
        }
        return "colaboraciones/chat";
    }
}
