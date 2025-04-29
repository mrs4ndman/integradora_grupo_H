package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.EspecialidadesEmpleado;
import org.grupo_h.comun.entity.auxiliar.Pais;
import org.hibernate.validator.constraints.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EspecialidadesEmpleadoRepository extends JpaRepository<EspecialidadesEmpleado, UUID> {
}
