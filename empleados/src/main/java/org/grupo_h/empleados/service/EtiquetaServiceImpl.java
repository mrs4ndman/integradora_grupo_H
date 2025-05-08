package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.Etiqueta;
import org.grupo_h.comun.repository.EtiquetaRepository;
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

    @Autowired
    public EtiquetaServiceImpl(EtiquetaRepository etiquetaRepository) {
        this.etiquetaRepository = etiquetaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Etiqueta> findById(UUID id) {
        return etiquetaRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Etiqueta> findAll() {
        return etiquetaRepository.findAll();
    }

    @Override
    @Transactional
    public Etiqueta findOrCreateEtiqueta(String nombreEtiqueta) {
        String nombreNormalizado = nombreEtiqueta.trim();
        if (nombreNormalizado.isEmpty()) {
            throw new IllegalArgumentException("El nombre de la etiqueta no puede estar vacío");
        }

        return etiquetaRepository.findByNombreIgnoreCase(nombreNormalizado)
                .orElseGet(() -> {
                    Etiqueta nuevaEtiqueta = new Etiqueta(nombreNormalizado);
                    return etiquetaRepository.save(nuevaEtiqueta);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Etiqueta> searchByNombreStartingWith(String prefix) {
        return etiquetaRepository.findByNombreStartingWithIgnoreCase(prefix.trim());
    }

    @Override
    @Transactional
    public List<Etiqueta> findOrCreateEtiquetas(List<String> nombresEtiquetas) {
        return nombresEtiquetas.stream()
                .map(this::findOrCreateEtiqueta)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Etiqueta> findByIds(List<UUID> ids) {
        return etiquetaRepository.findAllById(ids);
    }
}
