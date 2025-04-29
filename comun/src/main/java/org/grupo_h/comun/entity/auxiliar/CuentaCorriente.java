package org.grupo_h.comun.entity.auxiliar;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que representa una cuenta corriente en el sistema.
 * Esta clase es embedible y puede ser utilizada como parte de otra entidad.
 */
@Data
@NoArgsConstructor
@Embeddable
public class CuentaCorriente {

//    /**
//     * Id de la cuenta corriente.
//     */
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    /**
     * Número IBAN de la cuenta corriente.
     * Este campo es obligatorio y no puede ser nulo.
     */
    @Column(name = "cuenta_corriente",
            nullable = false,
            unique = true)
    private String cuentaCorriente;

    /**
     * Entidad bancaria asociada a esta cuenta.
     * Relación ManyToOne: Muchas cuentas pueden pertenecer a un mismo banco.
     * - Clave foránea: banco_codigo_entidad_bancaria
     * - Nombre explícito para la constraint FK
     * - FetchType.LAZY para mejor rendimiento
     */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banco_codigo_entidad_bancaria",
            foreignKey = @ForeignKey(name = "FK_CUENTA_ENTIDAD_BANCARIA"))
    private EntidadBancaria entidadBancaria;


}
