package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.auxiliar.EntidadBancaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EntidadBancariaRepository extends JpaRepository<EntidadBancaria, UUID> {
    Optional<EntidadBancaria> findByNombreEntidad(String nombreEntidadDTO);
}
