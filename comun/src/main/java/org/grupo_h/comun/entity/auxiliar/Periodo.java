package org.grupo_h.comun.entity.auxiliar;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.time.LocalDate;

/**
 * Representa un período de tiempo con fecha de inicio y fin.
 */
@Embeddable
@Data
public class Periodo {
    /** Fecha de inicio del período. */
    private LocalDate fechaInicio;

    /** Fecha de fin del período. */
    private LocalDate fechaFin;

    protected Periodo() {
    }

    private Periodo(LocalDate fechaInicio, LocalDate fechaFin) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    /**
     * Crea una instancia de Periodo con las fechas especificadas.
     *
     * @param fechaInicio Fecha de inicio.
     * @param fechaFin Fecha de fin.
     * @return Instancia de Periodo.
     */
    public static Periodo of(LocalDate fechaInicio, LocalDate fechaFin) {
        return new Periodo(fechaInicio, fechaFin);
    }
}
