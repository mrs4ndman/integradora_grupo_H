package org.grupo_h.empleados.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.grupo_h.empleados.validation.ContrasenaCoincide;

/**
 * DTO que representa los datos de registro de un usuario.
 */
@Data
@ContrasenaCoincide
public class UsuarioRegistroDTO {
    /**
     * Correo electrónico del usuario. Debe tener un formato válido.
     */
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    /**
     * Contraseña del usuario. No puede estar vacía.
     *  Debe tener entre 8 y 12 caracteres, e incluir al menos una mayúscula, una minúscula, un número y un carácter especial
     */
    @NotBlank(message = "La contraseña es obligatoria")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!._-]).{8,12}$",
            message = "La contraseña debe tener entre 8 y 12 caracteres, e incluir al menos una mayúscula, una minúscula, un número y un carácter especial.")
    private String contrasena;

    @NotBlank(message = "La contraseña es obligatoria")
    private String confirmarContrasena;

}
