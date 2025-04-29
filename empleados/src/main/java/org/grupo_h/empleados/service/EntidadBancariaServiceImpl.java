package org.grupo_h.empleados.service;


import org.grupo_h.comun.entity.auxiliar.EntidadBancaria;
import org.grupo_h.comun.repository.EntidadBancariaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class EntidadBancariaServiceImpl implements EntidadBancariaService {

    @Autowired
    private EntidadBancariaRepository entidadBancariaRepository;

    @Override
    public List<EntidadBancaria> listarEntidadBancaria() {
        return entidadBancariaRepository.findAll();
    }
}
