package org.grupo_h.empleados.dto.mensajeria;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensajeResponseDTO {

    private UUID id; // ID del mensaje

    private UUID idColaboracion;

    private UUID idEmisor;
    private String nombreEmisor;
    private String emailEmisor;

    private UUID idReceptor; // El otro participante en la colaboración
    private String nombreReceptor;
    private String emailReceptor;

    private String contenido;
    private LocalDateTime fechaEmision;

    private UUID idMensajeRespuestaA; // Si este mensaje es una respuesta
    private String contenidoMensajeRespuestaA; // Contenido del mensaje al que se responde (opcional, para UI)
}