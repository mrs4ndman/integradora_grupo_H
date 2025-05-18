package org.grupo_h.empleados.controller;

import org.grupo_h.empleados.dto.notificacion.NotificacionDTO;
import org.grupo_h.empleados.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/empleados/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @GetMapping
    public String listarNotificaciones(
            Model model,
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10, sort = "fechaHora") Pageable pageable) {
        if (userDetails == null) {
            return "redirect:/usuarios/inicio-sesion"; // O tu página de login
        }
        Page<NotificacionDTO> paginaNotificaciones = notificacionService.getNotificacionesParaEmpleado(userDetails.getUsername(), pageable);
        model.addAttribute("paginaNotificaciones", paginaNotificaciones);
        model.addAttribute("conteoNoLeidas", notificacionService.contarNotificacionesNoLeidas(userDetails.getUsername()));
        // Puedes añadir otros atributos necesarios para la vista
        return "notificaciones/lista"; // Crear este HTML
    }

    @PostMapping("/{idNotificacion}/marcar-leida")
    public String marcarComoLeida(
            @PathVariable UUID idNotificacion,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/usuarios/inicio-sesion";
        }
        try {
            notificacionService.marcarNotificacionComoLeida(idNotificacion, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Notificación marcada como leída.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al marcar la notificación: " + e.getMessage());
        }
        return "redirect:/empleados/notificaciones";
    }

    @PostMapping("/marcar-todas-leidas")
    public String marcarTodasComoLeidas(
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/usuarios/inicio-sesion";
        }
        try {
            notificacionService.marcarTodasLasNotificacionesComoLeidas(userDetails.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Todas las notificaciones marcadas como leídas.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al marcar las notificaciones: " + e.getMessage());
        }
        return "redirect:/empleados/notificaciones";
    }

    // Endpoint opcional para obtener el conteo (para AJAX si lo necesitas)
    @GetMapping("/conteo-no-leidas")
    @ResponseBody
    public long getConteoNoLeidas(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return 0;
        }
        return notificacionService.contarNotificacionesNoLeidas(userDetails.getUsername());
    }
}