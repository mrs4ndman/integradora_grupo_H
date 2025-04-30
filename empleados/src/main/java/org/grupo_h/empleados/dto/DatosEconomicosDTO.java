package org.grupo_h.empleados.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.grupo_h.empleados.Validaciones.GruposValidaciones.DatosFinancieros;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatosEconomicosDTO {
    /**
     * Salario del empleado.
     */
    @NotNull(message = "{Validacion.Salario.Notnull}",
            groups = DatosFinancieros.class)
    @Digits(integer = 8,
            fraction = 2,
            message = "{Validacion.Salario.Digits}",
            groups = DatosFinancieros.class)
    @PositiveOrZero(message = "{Validacion.Salario.PositiveOrZero}",
            groups = DatosFinancieros.class)
    private Double salarioDTO;

    /**
     * Comisión del empleado.
     */
    @NotNull(message = "{Validacion.Comision.Notnull}",
            groups = DatosFinancieros.class)
    @Digits(integer = 8,
            fraction = 2,
            message = "{Validacion.Comision.Digits}",
            groups = DatosFinancieros.class)
    @PositiveOrZero(message = "{Validacion.Comision.PositiveOrZero}",
            groups = DatosFinancieros.class)
    private Double comisionDTO;

//    @Valid
//    @NotNull(groups = DatosFinancieros.class)
//    private TarjetaCreditoDTO tarjetaCreditoDTO;

    @Valid
    private CuentaCorrienteDTO cuentaCorrienteDTO;
}
