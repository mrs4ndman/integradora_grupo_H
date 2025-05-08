package org.grupo_h.comun.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString; // Recomendable para evitar problemas con Lombok y relaciones bidireccionales

import java.util.ArrayList; // Inicializar colecciones
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Producto> productos = new ArrayList<>();
}