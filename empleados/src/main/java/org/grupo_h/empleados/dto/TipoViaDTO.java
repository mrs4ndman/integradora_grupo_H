package org.grupo_h.empleados.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.grupo_h.empleados.Validaciones.GruposValidaciones.DatosRegistroDireccion;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TipoViaDTO {
    @NotBlank(groups = DatosRegistroDireccion.class)
    private String codigoTipoVia = "C";
    @NotBlank(groups = DatosRegistroDireccion.class)
    private String tipoVia;
}
