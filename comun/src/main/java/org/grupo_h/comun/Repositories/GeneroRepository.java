package org.grupo_h.comun.Repositories;

import org.grupo_h.comun.entity.auxiliar.Genero;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GeneroRepository extends CrudRepository<Genero, Long> {

    Optional<Object> findByCodigoGenero(String genero);
}
