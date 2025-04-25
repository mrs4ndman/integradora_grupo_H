package org.grupo_h.comun.Repositories;

import org.grupo_h.comun.entity.auxiliar.Pais;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaisRepository extends JpaRepository<Pais, String> {
}
