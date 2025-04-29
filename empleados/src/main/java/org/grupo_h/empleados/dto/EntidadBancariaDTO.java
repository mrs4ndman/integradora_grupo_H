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
public class EntidadBancariaDTO {

    private Long id;

    @Pattern(
            regexp = "^(SAN|BBVA|CABK|SABE)$\n",
            message = "{Validacion.codigoEntidadBancaria.notBlank}")
    private String codigoEntidadBancaria;

    @NotBlank(message = "El nombre de la entidad bancaria es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombreEntidadBancaria;

    private String siglasEntidad;

    private String codigoPais;
}
