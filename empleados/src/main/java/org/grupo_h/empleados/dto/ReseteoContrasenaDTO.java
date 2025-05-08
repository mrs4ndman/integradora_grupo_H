package org.grupo_h.empleados.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.grupo_h.empleados.Validaciones.ValidacionesPersonalizadas.ContrasenaCoincide;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ContrasenaCoincide
public class ReseteoContrasenaDTO {

    @NotBlank(message = "La contraseña es obligatoria")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!._-]).{8,12}$",
            message = "La contraseña debe tener entre 8 y 12 caracteres, e incluir al menos una mayúscula, una minúscula, un número y un carácter especial.")
    private String nuevaContraseña;

     @NotBlank(message = "Confirma la nueva contraseña.")
     private String confirmarNuevaContraseña;
}