package org.grupo_h.empleados.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignarEtiquetaRequestDTO {

    @NotEmpty(message = "Debe seleccionar al menos un empleado.")
    private List<UUID> empleadoIds;

    @NotBlank(message = "El nombre de la etiqueta no puede estar vacío.")
    private String nombreEtiqueta;
}