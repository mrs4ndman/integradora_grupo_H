package org.grupo_h.administracion.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO que representa los datos de registro de un usuario.
 */
@Data
public class AdministradorRegistroDTO {
    /**
     * Correo electrónico del usuario. Debe tener un formato válido.
     */
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    /**
     * Contraseña del usuario. No puede estar vacía.
     */
    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;

}
