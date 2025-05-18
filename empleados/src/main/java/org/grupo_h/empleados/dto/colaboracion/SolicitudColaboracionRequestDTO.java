package org.grupo_h.empleados.dto.colaboracion;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotNull; // Ya no se usa para idEmpleadoDestino
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
// import java.util.UUID; // Ya no se usa para idEmpleadoDestino

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudColaboracionRequestDTO {

    // Se cambia idEmpleadoDestino por emailEmpleadoDestino
    // @NotNull(message = "El ID del empleado destino no puede ser nulo.")
    // private UUID idEmpleadoDestino;

    @NotBlank(message = "El email del empleado destino no puede estar vacío.")
    @Email(message = "Debe proporcionar un formato de email válido para el empleado destino.")
    private String emailEmpleadoDestino; // Email del Usuario asociado al Empleado receptor

    private String mensaje; // Mensaje opcional adjunto a la solicitud

    // Campos opcionales si se quiere proponer un periodo específico desde la solicitud
    private LocalDateTime fechaInicioPeriodoPropuesto;
    private LocalDateTime fechaFinPeriodoPropuesto;
}
