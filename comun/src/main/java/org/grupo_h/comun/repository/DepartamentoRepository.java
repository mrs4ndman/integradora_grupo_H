package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, UUID> {
    Optional<Object> findByNombreDept(String nombreDept);
}
