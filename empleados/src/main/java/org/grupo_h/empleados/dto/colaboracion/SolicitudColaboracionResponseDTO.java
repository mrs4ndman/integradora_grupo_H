package org.grupo_h.empleados.dto.colaboracion;

import lombok.Data;
import lombok.Getter;
import org.grupo_h.comun.entity.chat.SolicitudColaboracion.EstadoSolicitud;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SolicitudColaboracionResponseDTO {
    private UUID id; // ID de la solicitud

    private UUID idEmpleadoEmisor;
    private String nombreEmpleadoEmisor; // Añadir si es util
    private String emailEmpleadoEmisor;

    private UUID idEmpleadoReceptor;
    private String nombreEmpleadoReceptor;
    private String emailEmpleadoReceptor;

    private LocalDateTime fechaSolicitud;
    private EstadoSolicitud estado;
    private String mensaje; // Mensaje de la solicitud original

    private LocalDateTime fechaInicioPeriodo;
    private LocalDateTime fechaFinPeriodo;

    // Si la solicitud fue aceptada y generó una colaboración
    private UUID colaboracionId;
}
