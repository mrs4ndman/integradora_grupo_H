package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.Empleado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    Optional<Empleado> findByNombreAndActivoTrue(String nombreEmpleado);
//    Optional<Empleado> findByNombreEmpleado(String nombreEmpleado);

    List<Empleado> findByJefeAndActivoTrue(Empleado jefe);

    List<Empleado> findByJefeIdAndActivoTrue(UUID jefeId);

    Optional<Object> findByUsuarioId(UUID id);

    Optional<Empleado> findById(UUID etiquetaId);

    List<Empleado> findByActivoTrueAndNombreContainingOrActivoTrueAndApellidosContaining(String criterio, String criterio1);

    List<Empleado> findByActivoTrueAndNombreContainingIgnoreCase(String nombreDTO);

    List<Empleado> findByActivoTrueAndFechaNacimientoBetween(LocalDate fechaMin, LocalDate fechaMax);;

    Optional<Empleado> findByNumeroDocumentoAndActivoTrue(String numeroDni);

    Page<Empleado> findByNombreContainingIgnoreCaseOrApellidosContainingIgnoreCaseOrNumeroDocumentoContainingIgnoreCase(String nombre, String apellidos, String numeroDocumento, Pageable pageable);


    Optional<Empleado> findByNumeroDocumento(String numeroDni);

    List<Empleado> findAll(Specification<Empleado> spec, Sort sort);

    Page<Empleado> findAll(Pageable pageable);

    Page<Empleado> findByActivo(boolean activo, Pageable pageable);

    void deleteByNumeroDocumento(String numeroDocumento);
}
