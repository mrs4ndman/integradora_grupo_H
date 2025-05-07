package org.grupo_h.empleados.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.grupo_h.empleados.Validaciones.GruposValidaciones.DatosRegistroDireccion;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TipoViaDTO {

    private UUID id;

    @NotNull(groups = DatosRegistroDireccion.class)
    private String tipoVia = "Calle";
}
