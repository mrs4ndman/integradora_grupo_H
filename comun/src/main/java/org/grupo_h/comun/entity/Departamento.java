package org.grupo_h.comun.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Departamento {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false, unique = true)
    private String nombreDept;


    // Constructor para crear departamentos
    public Departamento(String codigo, String nombreDept) {
        this.codigo = codigo;
        this.nombreDept = nombreDept;
    }

    public String toStringBonito() {
        return "Nombre: " +  nombreDept +  " | Codigo: "  + codigo;
    }
}
