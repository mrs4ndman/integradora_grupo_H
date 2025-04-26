package org.grupo_h.empleados.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DatosBancariosDTO {
    @Pattern(
            regexp = "^(SAN|BBVA|CABK|SABE)$\n",
            message = "{Validacion.codigoEntidadBancaria.notBlank}")
    private String entidadBancariaDTO;

    private String cuentaCorrienteDTO;

    @Digits(integer = 8, fraction = 2, message = "{Validacion.salario.Digits}")
    private Float salario;

    @Digits(integer = 8, fraction = 2, message = "{Validacion.salario.Digits}")
    private Float comision;


}
