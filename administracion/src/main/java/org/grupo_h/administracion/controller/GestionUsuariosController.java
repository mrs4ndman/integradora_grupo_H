package org.grupo_h.administracion.controller;

import org.grupo_h.administracion.service.ParametrosService;
import org.grupo_h.administracion.service.UsuarioService;
import org.grupo_h.comun.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/administrador")
public class GestionUsuariosController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ParametrosService parametrosService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

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
}
