package org.grupo_h.empleados.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.grupo_h.empleados.Validaciones.GruposValidaciones.DatosFinancieros;
import org.grupo_h.empleados.Validaciones.GruposValidaciones.DatosPersonales;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaisDTO {

    @NotNull(groups = {DatosPersonales.class, DatosFinancieros.class})
    private String codigoPaisDTO = "ES";

    @NotNull(message = "El nombre del país es obligatorio",
            groups = {DatosPersonales.class, DatosFinancieros.class})
    private String nombrePaisDTO;

    @NotNull(message = "El prefijo del país es obligatorio",
            groups = {DatosPersonales.class, DatosFinancieros.class})
    private String prefijoPaisDTO;
}
