package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.auxiliar.TipoDocumento;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TipoDocumentoService {
    List<TipoDocumento> obtenertodosTiposDocumento();
}
