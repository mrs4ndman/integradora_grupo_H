package org.grupo_h.empleados.dto.colaboracion;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PeriodoColaboracionDTO {
    private UUID id;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin; // Puede ser null si el periodo está activo

    private UUID empleadoInicioId; // ID del empleado que formalmente inició/aceptó este periodo
    private String nombreEmpleadoInicio;

    private UUID empleadoFinId; // ID del empleado que formalmente terminó este periodo (si aplica)
    private String nombreEmpleadoFin;
}
