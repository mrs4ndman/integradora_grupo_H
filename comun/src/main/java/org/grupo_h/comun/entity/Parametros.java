package org.grupo_h.comun.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor @AllArgsConstructor
@Data
@Entity
public class Parametros {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String clave;

    @Column(unique = true)
    private String valor;

    public Parametros(String clave, String valor) {
        this.clave = clave;
        this.valor = valor;
    }
}
