package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, UUID> {
    Optional<Empleado> findByNombre(String nombreEmpleado);
//    Optional<Empleado> findByNombreEmpleado(String nombreEmpleado);

    List<Empleado> findByJefe(Empleado jefe);

    List<Empleado> findByJefeId(UUID jefeId);

    Optional<Object> findByUsuarioId(UUID id);

    Optional<Empleado> findById(UUID etiquetaId);
}
