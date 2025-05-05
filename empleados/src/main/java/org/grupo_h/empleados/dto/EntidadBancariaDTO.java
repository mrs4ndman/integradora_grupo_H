package org.grupo_h.empleados.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.grupo_h.empleados.Validaciones.GruposValidaciones.DatosFinales;
import org.grupo_h.empleados.Validaciones.GruposValidaciones.DatosFinancieros;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntidadBancariaDTO {

    private Long id;

    @Pattern(
            regexp = "^(SAN|BBVA|CABK|SABE)$\n",
            message = "{Validacion.codigoEntidadBancaria.notBlank}",
            groups = DatosFinancieros.class)
    private String codigoEntidadDTO;

    @NotNull(message = "{Validacion.entidadBancaria.NotNull}", groups = DatosFinancieros.class)
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombreEntidadDTO;

    @NotNull(message = "{Validacion.entidadBancaria.NotNull}", groups = DatosFinancieros.class)
    private String siglasDTO;

    @Valid
    private PaisDTO codigoPais;
}
