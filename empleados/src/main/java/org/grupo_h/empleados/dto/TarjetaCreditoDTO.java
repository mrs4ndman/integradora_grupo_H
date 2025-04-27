package org.grupo_h.empleados.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.LuhnCheck;

@Data
public class TarjetaCreditoDTO {

    @Pattern(
            regexp = "^(V|AM|MC)$",
            message = "{Validacion.tarjetaCredito.notBlank}")
    private String tipoTarjetaCreditoDTO;

    // Validacion algoritmo Luhn
    @LuhnCheck(message = "{Validacion.tarjetaCredito.formatoInvalido}")
    private String numeroTarjetaCreditoDTO;

    @Pattern(
            regexp = "^(0[1-9]|1[0-2])$",
            message = "{Validacion.tarjetaCredito.mes}")
    private String mesCaducidadTarjetaCreditoDTO;

    @Pattern(
            regexp = "^\\d{2}$",
            message = "{Validacion.tarjetaCredito.anio}")
    private String anioCaducidadTarjetaCreditoDTO;

    @Pattern(
            regexp = "^\\d{3,4}$",
            message = "{Validacion.tarjetaCredito.cvc}")
    private String CVCDTO;
}
