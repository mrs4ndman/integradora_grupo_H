package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.auxiliar.Genero;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GeneroRepository extends CrudRepository<Genero, String> {
    Optional<Object> findByCodigoGenero(String genero);
}
