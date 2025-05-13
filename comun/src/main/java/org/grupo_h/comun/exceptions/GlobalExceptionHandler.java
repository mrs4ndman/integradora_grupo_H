package org.grupo_h.comun.exceptions;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntidadDuplicadaEnSesionException.class)
    public String manejarEntidadDuplicada(EntidadDuplicadaEnSesionException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "empleado/empleadoDatosFinales";
    }
}
