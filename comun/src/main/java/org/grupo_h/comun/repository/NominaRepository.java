package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.Nomina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NominaRepository extends JpaRepository<Nomina, UUID> {
    @Override
    Optional<Nomina> findById(UUID id);
}
