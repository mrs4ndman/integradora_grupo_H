package org.grupo_h.administracion.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.grupo_h.administracion.dto.EmpleadoSimpleDTO;
import org.grupo_h.administracion.service.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/administrador")
public class GestionSubordinadosController {

    /* ------------------------ GESTION DE SUBORDINADOS ----------------------------- */
    @Autowired
    private EmpleadoService empleadoService;

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
}
