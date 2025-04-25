package org.grupo_h.comun.entity.auxiliar;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Clase que representa una cuenta corriente en el sistema.
 * Esta clase es embedible y puede ser utilizada como parte de otra entidad.
 */
@Data
@Entity
public class CuentaCorriente {

    /**
     * Id de la cuenta corriente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Número de la cuenta corriente.
     * Este campo es obligatorio y no puede ser nulo.
     */
    @Column(name = "IBAN_Cuenta_Corriente", nullable = false)
    private String cuentaCorriente;

    /**
     * Nombre del banco asociado a la cuenta corriente.
     * Este campo es obligatorio y no puede ser nulo.
     */

    @ManyToOne
    @JoinColumn(name = "banco_codigo_entidad_bancaria",
            foreignKey = @ForeignKey(name = "FK_ENTIDAD_BANCARIA"))
    private EntidadBancaria banco;
}
