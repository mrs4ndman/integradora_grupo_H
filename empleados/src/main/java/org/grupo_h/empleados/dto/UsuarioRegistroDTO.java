package org.grupo_h.empleados.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO que representa los datos de registro de un usuario.
 */
@Data
public class UsuarioRegistroDTO {

    /**
     * Nombre de usuario. No puede estar vacío.
     */
    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String nombreUsuario;

    /**
     * Contraseña del usuario. No puede estar vacía.
     */
    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;

    /**
     * Correo electrónico del usuario. Debe tener un formato válido.
     */
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;
}
