package org.grupo_h.comun.entity.auxiliar;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.time.LocalDate;

@Embeddable
@Data
public class Periodo {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    protected Periodo() {
    }

    private Periodo(LocalDate fechaInicio, LocalDate fechaFin) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public static Periodo of(LocalDate fechaInicio, LocalDate fechaFin) {
        return new Periodo(fechaInicio, fechaFin);
    }
}
