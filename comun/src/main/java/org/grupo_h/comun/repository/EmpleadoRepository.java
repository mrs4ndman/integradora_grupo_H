package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.Empleado;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, UUID>, JpaSpecificationExecutor<Empleado> {
    Optional<Empleado> findByNombre(String nombreEmpleado);
//    Optional<Empleado> findByNombreEmpleado(String nombreEmpleado);

    List<Empleado> findByJefe(Empleado jefe);

    List<Empleado> findByJefeId(UUID jefeId);

    Optional<Object> findByUsuarioId(UUID id);

    Optional<Empleado> findById(UUID etiquetaId);

    List<Empleado> findByNombreContainingOrApellidosContaining(String criterio, String criterio1);

    List<Empleado> findByNombreContainingIgnoreCase(String nombreDTO);

    List<Empleado> findByFechaNacimientoBetween(LocalDate fechaMin, LocalDate fechaMax);;

    Optional<Empleado> findByNumeroDocumento(String numeroDni);

    List<Empleado> findAll(Specification<Empleado> spec, Sort sort);

    void deleteByNumeroDocumento(String numeroDocumento);
}
