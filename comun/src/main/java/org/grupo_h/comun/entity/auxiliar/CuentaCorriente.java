package org.grupo_h.comun.entity.auxiliar;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


@Entity
@Table(name = "cuenta_corriente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaCorriente {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "numero_cuenta", nullable = false)
    private String numeroCuenta;

    // Relación con Entidad Bancaria
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "entidad_bancaria_id", nullable = false)
    private EntidadBancaria entidadBancaria;
}
