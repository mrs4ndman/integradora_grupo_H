package org.grupo_h.empleados.dto;

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
    @NotNull
    @Pattern(regexp = "^[A-Z]{2}[0-9]{2}[A-Z0-9]{1,30}$",
            message = "{Validacion.cuentaCorriente.iban}")
    private String cuentaCorriente;

    /**
     * Nombre del banco asociado a la cuenta corriente. No puede ser nulo.
     */
    @NotNull
    private String banco;
}
