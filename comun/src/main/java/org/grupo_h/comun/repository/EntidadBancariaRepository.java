package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.auxiliar.EntidadBancaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntidadBancariaRepository extends JpaRepository<EntidadBancaria, Long> {
}
