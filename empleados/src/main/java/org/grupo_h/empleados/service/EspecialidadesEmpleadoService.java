package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.EspecialidadesEmpleado;
import org.grupo_h.empleados.dto.EspecialidadesEmpleadoDTO;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface EspecialidadesEmpleadoService {
    List<EspecialidadesEmpleado> obtenerTodasEspecialidades();
    List<EspecialidadesEmpleadoDTO> obtenerTodasEspecialidadesEmpleadoDTO();
}
