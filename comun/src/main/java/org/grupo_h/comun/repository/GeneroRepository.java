package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.auxiliar.Genero;
import org.grupo_h.comun.entity.auxiliar.Pais;
import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GeneroRepository extends JpaRepository<Genero, String> {
    Optional<Object> findByCodigoGenero(String genero);
}
