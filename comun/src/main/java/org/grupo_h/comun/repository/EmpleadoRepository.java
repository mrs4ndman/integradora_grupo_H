package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, UUID> {
//    Optional<Empleado> findByNombreEmpleado(String nombreEmpleado);
}
