package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.Etiqueta;
import org.grupo_h.empleados.dto.EtiquetaDTO; // Asegúrate que este DTO existe

import java.util.List;
import java.util.UUID;

public interface EtiquetaService {
    Etiqueta findOrCreateEtiqueta(String nombre);
    List<Etiqueta> findAll();
    List<EtiquetaDTO> findAllDTO();
    List<Etiqueta> findByNombreContaining(String term);
    List<Etiqueta> findEtiquetasUsadasPorSubordinadosDelJefe(UUID jefeId);
    Etiqueta guardarEtiqueta(Etiqueta etiqueta); // Método genérico para guardar
}
