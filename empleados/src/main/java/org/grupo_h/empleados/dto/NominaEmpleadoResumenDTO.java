package org.grupo_h.empleados.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NominaEmpleadoResumenDTO {
    private UUID id;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Double salarioNeto;
    private String periodoLiquidacion; // Ej: "Enero 2024"
}