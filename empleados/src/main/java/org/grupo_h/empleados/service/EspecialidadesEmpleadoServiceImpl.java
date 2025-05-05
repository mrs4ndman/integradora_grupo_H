package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.EspecialidadesEmpleado;
import org.grupo_h.comun.repository.EspecialidadesEmpleadoRepository;
import org.grupo_h.empleados.dto.EspecialidadesEmpleadoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class EspecialidadesEmpleadoServiceImpl implements EspecialidadesEmpleadoService {

    @Autowired
    private EspecialidadesEmpleadoRepository especialidadesEmpleadoRepository;

    @Override
    public List<EspecialidadesEmpleado> obtenerTodasEspecialidades() {
        return especialidadesEmpleadoRepository.findAll();
    }

    @Override
    public List<EspecialidadesEmpleadoDTO> obtenerTodasEspecialidadesEmpleadoDTO() {
        List<EspecialidadesEmpleado> entidades = especialidadesEmpleadoRepository.findAll();
        return IntStream.range(0, entidades.size())
                .mapToObj(i -> {
                    EspecialidadesEmpleado e = entidades.get(i);
                    return new EspecialidadesEmpleadoDTO(
                            e.getId(),
                            e.getEspecialidad(),
                            false,
                            i
                    );
                })
                .collect(Collectors.toList());
    }

}
