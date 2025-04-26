package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.Parametros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Importar Optional

@Repository
public interface ParametrosRepository extends JpaRepository<Parametros, Long> {
    Optional<Parametros> findByClave(String clave);
}
