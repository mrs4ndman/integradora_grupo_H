package org.grupo_h.comun.entity.chat;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.grupo_h.comun.entity.Empleado;

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

    /** Identificador único de la colaboración. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Empleado asociado a la colaboración. */
    @ManyToOne
    @JoinColumn(name = "empleado_id")
    private Empleado empleado;

    // Lista de períodos de colaboración (comentado).
    // @OneToMany(mappedBy = "colaboracion")
    // private List<PeriodoColaboracion> periodos;

    // Lista de solicitudes de colaboración (comentado).
    // @OneToMany(mappedBy = "colaboracion")
    // private List<SolicitudColaboracion> solicitudes;
}
