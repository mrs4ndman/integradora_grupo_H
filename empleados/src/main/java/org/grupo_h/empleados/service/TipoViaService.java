package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.auxiliar.TipoVia;
import org.grupo_h.empleados.dto.TipoViaDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TipoViaService {
    List<TipoVia>obtenertodasTipoVia();
}
