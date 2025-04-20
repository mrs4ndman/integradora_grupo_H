package org.grupo_h.administracion.service;

import org.grupo_h.administracion.dto.EmpleadoDetalleDTO;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public interface EmpleadoService {
    Optional<EmpleadoDetalleDTO> obtenerDetalleEmpleado(UUID id);
}
