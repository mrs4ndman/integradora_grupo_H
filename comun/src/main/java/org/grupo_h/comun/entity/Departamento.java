package org.grupo_h.comun.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.grupo_h.comun.entity.auxiliar.Direccion;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Departamento {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(
            name = "codigo_departamento",
            unique = true,
            nullable = false
    )
    private int codigoDepartamento;

    @Column(name = "departamento", nullable = false)
    private String nombreDepartamento;

    @Column(name = "direccion")
    private Direccion direccion;
}
