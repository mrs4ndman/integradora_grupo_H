package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.Etiqueta; // O el paquete donde esté Etiqueta
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EtiquetaRepository extends JpaRepository<Etiqueta, UUID> {

    Optional<Etiqueta> findByNombreIgnoreCase(String nombre);

    List<Etiqueta> findByNombreStartingWithIgnoreCase(String prefix);

    List<Etiqueta> findByNombreInIgnoreCase(List<String> nombres);
}
