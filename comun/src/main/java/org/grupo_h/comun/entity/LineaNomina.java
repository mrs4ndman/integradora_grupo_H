package org.grupo_h.comun.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor @NoArgsConstructor
@Entity
@Table(name = "linea_nomina")
public class LineaNomina {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String concepto;
    private Double porcentaje; // Nuevo campo
    private Double cantidad; // Cambiado de int a Double

    @ManyToOne
    @JoinColumn(name = "nomina_id", nullable = false)
    private Nomina nomina;
}