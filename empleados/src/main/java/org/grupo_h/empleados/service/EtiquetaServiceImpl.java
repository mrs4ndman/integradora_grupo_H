package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.Etiqueta;
import org.grupo_h.comun.repository.EtiquetaRepository;
import org.grupo_h.empleados.dto.EtiquetaDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EtiquetaServiceImpl implements EtiquetaService {

    private final EtiquetaRepository etiquetaRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public EtiquetaServiceImpl(EtiquetaRepository etiquetaRepository, ModelMapper modelMapper) {
        this.etiquetaRepository = etiquetaRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public Etiqueta findOrCreateEtiqueta(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la etiqueta no puede ser vacío.");
        }
        String nombreNormalizado = nombre.trim();
        Optional<Etiqueta> etiquetaExistente = etiquetaRepository.findByNombre(nombreNormalizado);
        if (etiquetaExistente.isPresent()) {
            return etiquetaExistente.get();
        } else {
            Etiqueta nuevaEtiqueta = new Etiqueta();
            nuevaEtiqueta.setNombre(nombreNormalizado);
            return etiquetaRepository.save(nuevaEtiqueta);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Etiqueta> findAll() {
        return etiquetaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EtiquetaDTO> findAllDTO() {
        return etiquetaRepository.findAll().stream()
                .map(etiqueta -> modelMapper.map(etiqueta, EtiquetaDTO.class))
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<Etiqueta> findByNombreContaining(String term) {
        return etiquetaRepository.findByNombreContainingIgnoreCaseOrderByNombreAsc(term);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Etiqueta> findEtiquetasUsadasPorSubordinadosDelJefe(UUID jefeId) {
        return etiquetaRepository.findEtiquetasUsadasPorSubordinadosDelJefe(jefeId);
    }

    @Override
    @Transactional
    public Etiqueta guardarEtiqueta(Etiqueta etiqueta) {
        if (etiqueta.getNombre() == null || etiqueta.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la etiqueta no puede ser vacío.");
        }
        return etiquetaRepository.save(etiqueta);
    }
}