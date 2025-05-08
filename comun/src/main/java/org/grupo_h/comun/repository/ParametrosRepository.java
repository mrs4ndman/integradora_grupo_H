package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.Parametros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Importar Optional
import java.util.UUID;

@Repository
public interface ParametrosRepository extends JpaRepository<Parametros, UUID> {
    Optional<Parametros> findByClave(String clave);
}
