package org.grupo_h.empleados.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CuentaCorrienteDTO {

    @NotNull
    @Size(min = 20, max = 20, message = "{Validadcion.cuentaCorrienteDTO.cuentaCorriente}")
    private String cuentaCorriente;
    @NotNull
    private String banco;
}
