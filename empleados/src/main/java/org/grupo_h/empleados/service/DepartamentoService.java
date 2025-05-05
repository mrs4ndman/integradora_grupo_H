package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.Departamento;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface DepartamentoService {
    List<Departamento> obtenerTodosDepartamentos();
}
