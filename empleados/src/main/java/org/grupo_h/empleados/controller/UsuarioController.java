package org.grupo_h.empleados.controller;

import jakarta.validation.Valid;
import org.grupo_h.empleados.dto.UsuarioRegistroDTO;
import org.grupo_h.empleados.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
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
}
