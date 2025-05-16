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

    @Column(name = "fecha_inicio_nomina", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin_nomina", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "numero_seguridad_social_empleado") // Opcional
    private String numeroSeguridadSocialEmpleado;

    @Column(name = "puesto_empleado_nomina", nullable = true) // Puesto del empleado para esta nómina específica
    private String puestoEmpleadoNomina;

    @OneToMany(
            mappedBy = "nomina",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER)
    private List<LineaNomina> lineas;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado; // Relación inversa

    @Transient
    private Double cantidadBrutaAcumuladaAnual;

    @Transient
    private Double cantidadPercibidaAcumuladaAnual;

    @Transient
    private Double retencionesAcumuladasAnual;

    @Transient // No persistido, calculado al vuelo
    public Double getTotalDevengos() {
        return lineas.stream()
                .filter(l -> l.getCantidad() != null && l.getCantidad() > 0)
                .mapToDouble(LineaNomina::getCantidad)
                .sum();
    }

    @Transient
    public Double getTotalDeducciones() {
        return lineas.stream()
                .filter(l -> l.getCantidad() != null && l.getCantidad() < 0)
                .mapToDouble(LineaNomina::getCantidad)
                .sum();
    }

    @Transient
    public Double getSalarioNeto() {
        return getTotalDevengos() + getTotalDeducciones();
    }

    @Transient
    public boolean tieneSalarioBaseValido() {
        return lineas.stream()
                .anyMatch(l -> "Salario base".equalsIgnoreCase(l.getConcepto()) &&
                        l.getCantidad() != null &&
                        l.getCantidad() > 0);
    }
}
