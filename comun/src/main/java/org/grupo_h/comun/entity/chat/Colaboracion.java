package org.grupo_h.comun.entity.chat;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.grupo_h.comun.entity.Empleado;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Representa una colaboración en el sistema de chat.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Colaboracion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "empleado_a_id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_COLABORACION_EMPLEADO_A"))
    private Empleado empleadoA;

    @ManyToOne
    @JoinColumn(name = "empleado_b_id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_COLABORACION_EMPLEADO_B"))
    private Empleado empleadoB;

    @OneToMany(mappedBy = "colaboracion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PeriodoColaboracion> periodos;

    @OneToMany(mappedBy = "colaboracion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SolicitudColaboracion> solicitudes;

    // Nuevos campos para el bloqueo de colaboración
    private LocalDateTime fechaBloqueoRechazo;
    private LocalDateTime fechaBloqueoCancelacion;

    @Enumerated(EnumType.STRING)
    private EstadoColaboracion estado;

    public enum EstadoColaboracion {
        ACTIVA,
        INACTIVA,
        BLOQUEADA
    }
}