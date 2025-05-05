package org.grupo_h.comun.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.grupo_h.comun.entity.auxiliar.CuentaCorriente;

@Data
@NoArgsConstructor
@Embeddable
public class DatosEconomicos {

    @Embedded
    private CuentaCorriente cuentaCorriente;

//    @Column(name="cuentaCorriente", table = "datosEconomicos")
//    private String cuentaCorriente;

    @Column(name="salario", table = "datosEconomicos")
    private Double salario;

    @Column(name="comision", table = "datosEconomicos")
    private Double comision;

}
