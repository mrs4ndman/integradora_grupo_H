package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.Etiqueta;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public interface EtiquetaService {

    Optional<Etiqueta> findById(UUID id);

    List<Etiqueta> findAll();

    Etiqueta findOrCreateEtiqueta(String nombreEtiqueta);

    List<Etiqueta> searchByNombreStartingWith(String prefix);

    List<Etiqueta> findOrCreateEtiquetas(List<String> nombresEtiquetas);

    List<Etiqueta> findByIds(List<UUID> ids);
}
