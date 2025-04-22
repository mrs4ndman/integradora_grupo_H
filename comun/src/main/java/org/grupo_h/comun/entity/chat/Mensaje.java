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
    @OneToOne
    private Empleado emisor;

    /** Empleado que recibe el mensaje. */
    @OneToOne
    private Empleado receptor;

    /** Contenido del mensaje. No puede estar vacío. */
    @NotBlank
    private String mensaje;
}
