package org.grupo_h.comun.entity.auxiliar;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class CuentaCorriente {
    @Column(name = "cuenta_corriente", nullable = false)
    private String cuentaCorriente;
    @Column(name = "banco", nullable = false)
    private String banco;
}
