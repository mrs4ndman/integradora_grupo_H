package org.grupo_h.empleados.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * DTO que representa los datos de una cuenta corriente.
 */
@Data
public class CuentaCorrienteDTO {

    /**
     * Número de la cuenta corriente. Debe tener exactamente 20 caracteres.
     */
    @NotBlank(message = "{Validacion.numeroCuenta.NotBlank}")
    @Pattern(regexp = "^[A-Z]{2}[0-9]{2}[A-Z0-9]{4}[0-9]{7}([A-Z0-9]?){0,16}$",
            message = "{Validacion.numeroCuenta.IBAN}")
    private String numeroCuentaDTO;


    /**
     * Nombre del banco asociado a la cuenta corriente. No puede ser nulo.
     */
    @Valid
    private EntidadBancariaDTO entidadBancaria;
}
