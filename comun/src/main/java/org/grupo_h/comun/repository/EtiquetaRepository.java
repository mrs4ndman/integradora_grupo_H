package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.Etiqueta; // O el paquete donde esté Etiqueta
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EtiquetaRepository extends JpaRepository<Etiqueta, UUID> {

    Optional<Etiqueta> findByNombreIgnoreCase(String nombre);

    List<Etiqueta> findByNombreStartingWithIgnoreCase(String prefix);

    List<Etiqueta> findByNombreInIgnoreCase(List<String> nombres);

    Optional<Etiqueta> findByNombre(String nombre);

    List<Etiqueta> findByNombreContainingIgnoreCaseOrderByNombreAsc(String nombre);

    // Encuentra etiquetas que han sido usadas por los subordinados de un jefe específico
    @Query("SELECT DISTINCT et FROM Empleado e JOIN e.etiquetas et WHERE e.jefe.id = :jefeId ORDER BY et.nombre ASC")
    List<Etiqueta> findEtiquetasUsadasPorSubordinadosDelJefe(@Param("jefeId") UUID jefeId);
}
