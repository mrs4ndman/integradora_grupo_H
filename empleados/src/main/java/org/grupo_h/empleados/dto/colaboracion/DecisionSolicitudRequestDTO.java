package org.grupo_h.empleados.dto.colaboracion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DecisionSolicitudRequestDTO {
    @NotBlank(message = "La acción no puede estar vacía.")
    @Pattern(regexp = "ACEPTAR|RECHAZAR", message = "La acción debe ser ACEPTAR o RECHAZAR.")
    private String accion; // Podria ser un enum
    private String mensajeRespuesta;
}
