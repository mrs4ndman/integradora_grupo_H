package org.grupo_h.comun.entity.auxiliar;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

/**
 * Clase que representa una cuenta corriente en el sistema.
 * Esta clase es embedible y puede ser utilizada como parte de otra entidad.
 */
@Data
@Embeddable
public class CuentaCorriente {

    /**
     * Número de la cuenta corriente.
     * Este campo es obligatorio y no puede ser nulo.
     */
    @Column(name = "cuenta_corriente", nullable = false)
    private String cuentaCorriente;

    /**
     * Nombre del banco asociado a la cuenta corriente.
     * Este campo es obligatorio y no puede ser nulo.
     */
    @Column(name = "banco", nullable = false)
    private String banco;
}
