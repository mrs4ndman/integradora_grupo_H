package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.auxiliar.Genero;
import org.grupo_h.empleados.dto.EmpleadoRegistroDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GeneroService {
    List<Genero> obtenerGeneros();
    void asignarPrimerGeneroSiEsNulo(EmpleadoRegistroDTO empleadoRegistroDTO, List<Genero> generos);
}
