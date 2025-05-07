package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.auxiliar.TipoVia;
import org.grupo_h.comun.repository.TipoViaRepository;
import org.grupo_h.empleados.dto.TipoViaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoViaServiceImpl implements TipoViaService{

    @Autowired
    private TipoViaRepository tipoViaRepository;

    @Override
    public List<TipoVia> obtenertodasTipoVia() {
        return tipoViaRepository.findAll();
    }
}
