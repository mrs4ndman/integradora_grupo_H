package org.grupo_h.empleados.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.grupo_h.empleados.Validaciones.GruposValidaciones.DatosFinancieros;
import org.hibernate.validator.constraints.LuhnCheck;

@Data
public class TarjetaCreditoDTO {


    private String tipoTarjetaCreditoNombre;  // simple property for binding

    @Valid
    private TipoTarjetaCreditoDTO tipoTarjetaCreditoDTO;

    // Validacion algoritmo Luhn
    @LuhnCheck(message = "{Validacion.tarjetaCredito.formatoInvalido}",
            groups = DatosFinancieros.class)
    private String numeroTarjetaCreditoDTO;

    @Pattern(
            regexp = "^(0[1-9]|1[0-2])$",
            message = "{Validacion.tarjetaCredito.mes}",
            groups = DatosFinancieros.class)
    private String mesCaducidadTarjetaCreditoDTO;


    @Pattern(regexp = "^\\d{4}$", message = "El año debe tener 4 dígitos",
    groups = DatosFinancieros.class)
    @Min(value = 2025, message = "{Validacion.tarjetaCredito.anioMinimno}",
    groups = DatosFinancieros.class)
    @Max(value = 2045, message = "{Validacion.tarjetaCredito.anioMaximo}",
    groups = DatosFinancieros.class)
    private String anioCaducidadTarjetaCreditoDTO;

    @Pattern(
            regexp = "^\\d{3,4}$",
            message = "{Validacion.tarjetaCredito.cvc}",
    groups = DatosFinancieros.class)
    private String cvcDTO;
}
