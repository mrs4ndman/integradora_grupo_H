package org.grupo_h.empleados.service;


import org.grupo_h.comun.entity.auxiliar.EntidadBancaria;
import org.grupo_h.comun.repository.EntidadBancariaRepository;
import org.grupo_h.empleados.dto.EntidadBancariaDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EntidadBancariaServiceImpl implements EntidadBancariaService {

    @Autowired
    private EntidadBancariaRepository entidadBancariaRepository;
    @Autowired
    private final ModelMapper modelMapper;

    public EntidadBancariaServiceImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public List<EntidadBancaria> listarEntidadBancaria() {
        return entidadBancariaRepository.findAll();
    }

    @Override
    public Optional<EntidadBancariaDTO> findByNombreEntidad(String nombreEntidad) {
        List<EntidadBancaria> entidades = entidadBancariaRepository.findAll();
        List<EntidadBancariaDTO> entidadesDTO = new ArrayList<>();
        entidades.forEach(entidad -> entidadesDTO.add(modelMapper.map(entidad, EntidadBancariaDTO.class)));
        return entidadesDTO.stream()
                .filter(dto -> dto.getNombreEntidadDTO().equals(nombreEntidad))
                .findFirst();
    }

    public List<EntidadBancariaDTO> obtenerEntidadesBancarias(){
        List<EntidadBancaria>entidades=entidadBancariaRepository.findAll();
        List<EntidadBancariaDTO> entidadesDTO = new ArrayList<>();
        entidades.forEach(entidad -> entidadesDTO.add(modelMapper.map(entidad, EntidadBancariaDTO.class)));
        return entidadesDTO;
    }
}
