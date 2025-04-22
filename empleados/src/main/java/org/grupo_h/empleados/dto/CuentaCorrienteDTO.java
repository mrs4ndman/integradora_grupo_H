package org.grupo_h.empleados.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @Size(min = 20, max = 20, message = "{Validadcion.cuentaCorrienteDTO.cuentaCorriente}")
    private String cuentaCorriente;

    /**
     * Nombre del banco asociado a la cuenta corriente. No puede ser nulo.
     */
    @NotNull
    private String banco;
}
