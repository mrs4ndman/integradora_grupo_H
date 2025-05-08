package org.grupo_h.empleados.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EtiquetadoMasivoRequestDTO {

    @NotEmpty(message = "Debe seleccionar al menos un empleado.")
    private List<UUID> empleadoIds;

    @NotEmpty(message = "Debe seleccionar al menos una etiqueta.")
    private List<UUID> etiquetaIds;
}
