package org.grupo_h.comun.entity.chat;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.grupo_h.comun.entity.Empleado;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SolicitudColaboracion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "empleado_emisor_id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_SOLICITUD_EMISOR"))
    private Empleado empleadoEmisor;

    @ManyToOne
    @JoinColumn(name = "empleado_receptor_id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_SOLICITUD_RECEPTOR"))
    private Empleado empleadoReceptor;

    private LocalDateTime fechaSolicitud;

    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estado; // PENDING, ACCEPTED, REJECTED

    private String mensaje;

    @ManyToOne
    @JoinColumn(name = "colaboracion_id",
            foreignKey = @ForeignKey(name = "FK_SOLICITUD_COLABORACION"))
    private Colaboracion colaboracion;  // Nueva relación

    public enum EstadoSolicitud {
        PENDIENTE,
        ACEPTADA,
        RECHAZADA
    }
}