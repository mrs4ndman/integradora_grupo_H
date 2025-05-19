package org.grupo_h.comun.entity.chat;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.grupo_h.comun.entity.Empleado;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeriodoColaboracion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "colaboracion_id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_PERIODO_COLABORACION"))
    private Colaboracion colaboracion;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    @ManyToOne
    @JoinColumn(name = "empleado_inicio_id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_PERIODO_EMPLEADO_INICIO"))
    private Empleado empleadoInicio;

    @ManyToOne
    @JoinColumn(name = "empleado_fin_id",
            foreignKey = @ForeignKey(name = "FK_PERIODO_EMPLEADO_FIN"))
    private Empleado empleadoFin; // Puede ser null si no ha terminado
}