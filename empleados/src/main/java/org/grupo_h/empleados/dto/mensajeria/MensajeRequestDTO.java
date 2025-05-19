package org.grupo_h.empleados.dto.mensajeria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensajeRequestDTO {

    @NotNull(message = "El ID de la colaboración no puede ser nulo.")
    private UUID idColaboracion; // El mensaje debe pertenecer a una colaboración activa

    // El receptor se infiere de la colaboración y el emisor (el otro participante)
    // No es necesario enviar idReceptor si la colaboración es siempre entre 2 personas.

    @NotBlank(message = "El contenido del mensaje no puede estar vacío.")
    private String contenido;

    private UUID idMensajeRespuestaA; // Opcional: para responder a un mensaje específico
}