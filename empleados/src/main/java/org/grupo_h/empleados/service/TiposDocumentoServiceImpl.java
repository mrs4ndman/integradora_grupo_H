package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.auxiliar.TipoDocumento;
import org.grupo_h.comun.repository.TipoDocumentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TiposDocumentoServiceImpl implements TipoDocumentoService{

    @Autowired
    private TipoDocumentoRepository tipoDocumentoRepository;

    @Override
    public List<TipoDocumento> obtenertodosTiposDocumento() {
        return tipoDocumentoRepository.findAll();
    }
}
