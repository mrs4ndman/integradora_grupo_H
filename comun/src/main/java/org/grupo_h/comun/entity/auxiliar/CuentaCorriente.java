package org.grupo_h.comun.entity.auxiliar;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "cuenta_corriente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaCorriente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_cuenta", nullable = false)
    private String numeroCuenta;

    @Column(name = "iban", nullable = false, unique = true)
    private String iban;

    // Relación con Entidad Bancaria
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "entidad_bancaria_id", nullable = false)
    private EntidadBancaria entidadBancaria;
}
