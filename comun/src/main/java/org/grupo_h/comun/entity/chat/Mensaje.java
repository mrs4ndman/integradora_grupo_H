package org.grupo_h.comun.entity.chat;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.*;
import org.grupo_h.comun.entity.Empleado;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representa un mensaje en el sistema de chat.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Mensaje {

    /** Identificador único del mensaje. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Fecha y hora de emisión del mensaje. Debe ser una fecha pasada. */
    @Past
    private LocalDateTime fechaEmision;

    /** Empleado que envía el mensaje. */
    @ManyToOne
    @JoinColumn(name = "emisor_id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_MENSAJE_EMISOR"))
    private Empleado emisor;

    /** Empleado que recibe el mensaje. */
    @ManyToOne
    @JoinColumn(name = "receptor_id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_MENSAJE_RECEPTOR"))
    private Empleado receptor;

    /** Contenido del mensaje. No puede estar vacío. */
    @NotBlank
    private String mensaje;

    @ManyToOne
    @JoinColumn(name = "colaboracion_id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_MENSAJE_COLABORACION"))
    private Colaboracion colaboracion;

    @ManyToOne
    @JoinColumn(name = "respuesta_a_id",
            foreignKey = @ForeignKey(name = "FK_MENSAJE_RESPUESTA"))
    private Mensaje mensajeRespuesta; // Opcional:  Puede ser null
}