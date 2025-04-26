package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.auxiliar.Pais;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PaisRepository extends JpaRepository<Pais, String> {
}
