package org.grupo_h.empleados.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineaNominaDTO {
    private UUID id;
    private String concepto;
    private Double porcentaje;
    private Double cantidad;
}