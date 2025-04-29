package org.grupo_h.empleados.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.grupo_h.empleados.Validaciones.GruposValidaciones.DatosFinancieros;
import org.hibernate.validator.constraints.LuhnCheck;

@Data
public class TarjetaCreditoDTO {

    @Pattern(
            regexp = "^(V|AM|MC)$",
            message = "{Validacion.tarjetaCredito.notBlank}",
            groups = DatosFinancieros.class)
    private String tipoTarjetaCreditoDTO;

    // Validacion algoritmo Luhn
//    @LuhnCheck(message = "{Validacion.tarjetaCredito.formatoInvalido}",
//            groups = DatosFinancieros.class)
    private String numeroTarjetaCreditoDTO;

    @Pattern(
            regexp = "^(0[1-9]|1[0-2])$",
            message = "{Validacion.tarjetaCredito.mes}",
            groups = DatosFinancieros.class)
    private String mesCaducidadTarjetaCreditoDTO;


    @Pattern(regexp = "^\\d{4}$", message = "El año debe tener 4 dígitos",
    groups = DatosFinancieros.class)
    @Min(value = 2025, message = "{Validacion.tarjetaCredito.anioMinimo}",
    groups = DatosFinancieros.class)
    @Max(value = 2045, message = "{Validacion.tarjetaCredito.anioMaximo}",
    groups = DatosFinancieros.class)
    private String anioCaducidadTarjetaCreditoDTO;

    @Pattern(
            regexp = "^\\d{3,4}$",
            message = "{Validacion.tarjetaCredito.cvc}",
    groups = DatosFinancieros.class)
    private String CVCDTO;
}
