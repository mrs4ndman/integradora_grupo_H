package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.EspecialidadesEmpleado;
import org.grupo_h.comun.repository.EspecialidadesEmpleadoRepository;
import org.grupo_h.empleados.dto.EspecialidadesEmpleadoDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class EspecialidadesEmpleadoServiceImpl implements EspecialidadesEmpleadoService {

    @Autowired
    private EspecialidadesEmpleadoRepository especialidadesEmpleadoRepository;
    private final ModelMapper modelMapper;

    public EspecialidadesEmpleadoServiceImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }


    @Override
    public List<EspecialidadesEmpleado> obtenerTodasEspecialidades() {
        return especialidadesEmpleadoRepository.findAll();
    }

    @Override
    public List<EspecialidadesEmpleadoDTO> obtenerTodasEspecialidadesEmpleadoDTO() {
        return especialidadesEmpleadoRepository.findAll().stream()
                .map(e -> {
                    EspecialidadesEmpleadoDTO dto = new EspecialidadesEmpleadoDTO();
                    dto.setId(e.getId());
                    dto.setNombreEspecialidad(e.getEspecialidad());
                    dto.setSeleccionada(false);
                    return dto;
                })
                .collect(Collectors.toList());
    }


}
