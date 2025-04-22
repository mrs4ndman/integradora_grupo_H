package org.grupo_h.administracion.service;

import org.grupo_h.administracion.dto.EmpleadoDetalleDTO;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Servicio para gestionar operaciones relacionadas con empleados.
 * Proporciona métodos para obtener y manipular información de empleados.
 */
@Service
public interface EmpleadoService {
    /**
     * Obtiene información detallada de un empleado por su identificador.
     *
     * @param id Identificador único del empleado
     * @return Optional con los detalles del empleado si existe, o vacío si no se encuentra
     */
    Optional<EmpleadoDetalleDTO> obtenerDetalleEmpleado(UUID id);
}
