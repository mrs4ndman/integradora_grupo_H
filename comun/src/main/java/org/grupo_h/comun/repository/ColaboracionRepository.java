package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.comun.entity.chat.Colaboracion;
import org.grupo_h.comun.entity.chat.Colaboracion.EstadoColaboracion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ColaboracionRepository extends JpaRepository<Colaboracion, UUID> {

    // Encontrar una colaboración activa por su ID
    Optional<Colaboracion> findByIdAndEstado(UUID id, EstadoColaboracion estado);

    // Encontrar una colaboración (activa o no) entre dos empleados específicos (en cualquier orden de A o B)
    @Query("SELECT c FROM Colaboracion c WHERE " +
            "((c.empleadoA = :emp1 AND c.empleadoB = :emp2) OR (c.empleadoA = :emp2 AND c.empleadoB = :emp1))")
    Optional<Colaboracion> findColaboracionEntreEmpleados(@Param("emp1") Empleado emp1, @Param("emp2") Empleado emp2);

    // Encontrar una colaboración ACTIVA entre dos empleados específicos
    @Query("SELECT c FROM Colaboracion c WHERE " +
            "((c.empleadoA = :emp1 AND c.empleadoB = :emp2) OR (c.empleadoA = :emp2 AND c.empleadoB = :emp1)) " +
            "AND c.estado = :estado")
    Optional<Colaboracion> findColaboracionActivaEntreEmpleados(@Param("emp1") Empleado emp1, @Param("emp2") Empleado emp2, @Param("estado") EstadoColaboracion estado);

    // Listar colaboraciones activas para un empleado (donde es empleadoA o empleadoB), paginado
    @Query("SELECT c FROM Colaboracion c WHERE (c.empleadoA = :empleado OR c.empleadoB = :empleado) AND c.estado = :estado ORDER BY c.id DESC") // Podrías ordenar por fecha de último mensaje si lo tuvieras
    Page<Colaboracion> findColaboracionesActivasParaEmpleado(@Param("empleado") Empleado empleado, @Param("estado") EstadoColaboracion estado, Pageable pageable);

    // Listar todas las colaboraciones de un empleado (activas o no)
    Page<Colaboracion> findByEmpleadoAOrEmpleadoB(Empleado empleadoA, Empleado empleadoB, Pageable pageable);

    Optional<Colaboracion> findByEmpleadoAAndEmpleadoBOrEmpleadoBAndEmpleadoAAndEstado(Empleado emisor, Empleado receptor, Empleado emisor1, Empleado receptor1, EstadoColaboracion estadoColaboracion);

    Page<Colaboracion> findByEmpleadoAAndEstadoOrEmpleadoBAndEstado(Empleado empleado, EstadoColaboracion estadoColaboracion, Empleado empleado1, EstadoColaboracion estadoColaboracion1, Pageable pageable);
}