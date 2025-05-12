package org.grupo_h.comun.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Nomina {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "fecha_inicio_nomina")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin_nomina")
    private LocalDate fechaFin;

    @OneToMany(
            mappedBy = "nomina",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<LineaNomina> lineas;

    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado; // Relación inversa
}
