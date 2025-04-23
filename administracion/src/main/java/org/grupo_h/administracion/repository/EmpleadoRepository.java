package org.grupo_h.administracion.repository;

import org.grupo_h.comun.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repositorio para la entidad Empleado.
 * Proporciona operaciones de acceso a datos para los empleados en la base de datos.
 */
@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, UUID> {
}
