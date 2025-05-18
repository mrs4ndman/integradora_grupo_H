package org.grupo_h.empleados.dto.notificacion;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDTO {

    private UUID id;
    private String mensaje;
    private LocalDateTime fechaHoraCreacion;
    private boolean leida;
    private String tipoEvento;
    private UUID idReferenciaEntidad;
    private String urlReferencia;

    // Campo adicional para la vista, formateado amigablemente
    private String fechaHoraFormateada;
    private String tiempoTranscurrido; // Ej: "Hace 5 minutos", "Ayer", "Hace 2 días"
}
