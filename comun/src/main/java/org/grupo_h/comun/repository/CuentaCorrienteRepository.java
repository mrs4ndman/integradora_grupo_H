package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.auxiliar.CuentaCorriente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuentaCorrienteRepository extends JpaRepository<CuentaCorriente, Long> {
}
