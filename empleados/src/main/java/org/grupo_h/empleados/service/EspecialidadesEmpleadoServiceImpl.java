package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.EspecialidadesEmpleado;
import org.grupo_h.comun.repository.EspecialidadesEmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class EspecialidadesEmpleadoServiceImpl implements EspecialidadesEmpleadoService {

    @Autowired
    private EspecialidadesEmpleadoRepository especialidadesEmpleadoRepository;


    @Override
    public List<EspecialidadesEmpleado> findAll() {
        return especialidadesEmpleadoRepository.findAll();
    }
}
