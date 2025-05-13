package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.Categoria;
import org.grupo_h.comun.entity.auxiliar.CuentaCorriente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface CuentaCorrienteRepository extends JpaRepository<CuentaCorriente, UUID> {
}
