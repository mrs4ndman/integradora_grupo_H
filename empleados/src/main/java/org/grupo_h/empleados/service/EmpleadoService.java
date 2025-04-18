package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.empleados.dto.EmpleadoRegistroDTO;

import java.util.Optional;

public interface EmpleadoService {
    Empleado registrarEmpleado(EmpleadoRegistroDTO empleadoDTO);
    Optional<Empleado> findByNombreEmpleado(String nombreEmpleado);
}
