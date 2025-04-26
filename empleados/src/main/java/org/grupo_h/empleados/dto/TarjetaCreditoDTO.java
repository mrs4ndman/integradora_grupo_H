package org.grupo_h.empleados.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TarjetaCreditoDTO {

    @Pattern(
            regexp = "^(v|AM|MC)$\n",
            message = "{Validacion.codigoEntidadBancaria.notBlank}")
    private String tipoTarjetaCreditoDTO;

    // Validacion algoritmo Luhn
    private String numeroTarjetaCreditoDTO;

    private String mesCaducidadTarjetaCreditoDTO;

    private String anioCaducidadTarjetaCreditoDTO;

    private String CVCDTO;
}
