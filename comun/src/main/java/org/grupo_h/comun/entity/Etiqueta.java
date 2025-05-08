package org.grupo_h.comun.entity; // O el paquete que elijas, ej. org.grupo_h.comun.entity.etiquetas

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Etiqueta {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100) // Nombre único y no nulo
    private String nombre;

    public Etiqueta(String nombre) {
        this.nombre = nombre;
    }
}
