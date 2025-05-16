package org.grupo_h.empleados.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GestionarEtiquetasEmpleadoRequestDTO {

    @NotNull(message = "Debe seleccionar un empleado.")
    private UUID empleadoId;

    // Lista de IDs de etiquetas que se deben MANTENER para el empleado.
    // Si está vacía o nula, se podrían eliminar todas las etiquetas (depende de la lógica de servicio).
    private List<UUID> etiquetaIdsAMantener;
}