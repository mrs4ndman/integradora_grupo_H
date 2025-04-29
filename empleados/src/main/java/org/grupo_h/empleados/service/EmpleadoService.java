package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.empleados.dto.EmpleadoDetalleDTO;
import org.grupo_h.empleados.dto.EmpleadoRegistroDTO;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public interface EmpleadoService {
    Empleado registrarEmpleado(EmpleadoRegistroDTO empleadoDTO) throws Exception;

    Optional<Empleado> findByNombreEmpleado(String nombreEmpleado);

    Optional<EmpleadoDetalleDTO> obtenerDetalleEmpleado(UUID id);
}
