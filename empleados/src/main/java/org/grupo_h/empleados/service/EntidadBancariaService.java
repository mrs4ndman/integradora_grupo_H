package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.auxiliar.EntidadBancaria;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface EntidadBancariaService {
    List<EntidadBancaria> listarEntidadBancaria();

}
