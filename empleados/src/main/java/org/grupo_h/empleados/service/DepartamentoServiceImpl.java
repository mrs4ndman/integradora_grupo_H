package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.Departamento;
import org.grupo_h.comun.repository.DepartamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class DepartamentoServiceImpl implements DepartamentoService {

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Override
    public List<Departamento> findAll() {
        return departamentoRepository.findAll();
    }
}
