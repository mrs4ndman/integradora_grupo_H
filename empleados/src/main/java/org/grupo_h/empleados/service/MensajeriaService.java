package org.grupo_h.empleados.service;

import org.grupo_h.empleados.dto.mensajeria.MensajeRequestDTO;
import org.grupo_h.empleados.dto.mensajeria.MensajeResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MensajeriaService {
    MensajeResponseDTO enviarMensaje(MensajeRequestDTO dto, String emailEmisor);

    Page<MensajeResponseDTO> listarMensajesColaboracion(UUID idColaboracion, String emailUsuarioActual, Pageable pageable);
}
