package org.grupo_h.empleados.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaisDTO {

    @NotBlank(message = "El código de país es obligatorio")
    @Size(min = 2, max = 2, message = "El código debe tener exactamente 2 caracteres")
    @Pattern(regexp = "^[A-Z]{2}$", message = "El código debe contener 2 letras mayúsculas")
    private String codigoPais;

    @NotBlank(message = "El nombre del país es obligatorio")
    private String nombrePais;

    @NotBlank(message = "El prefijo del país es obligatorio")
    private String prefijoPais;
}
