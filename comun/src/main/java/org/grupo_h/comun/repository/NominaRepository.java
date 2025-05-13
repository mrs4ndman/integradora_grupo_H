package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.comun.entity.Nomina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NominaRepository extends JpaRepository<Nomina, UUID> {
    @Override
    Optional<Nomina> findById(UUID id);

    @Query("SELECT n FROM Nomina n WHERE n.empleado = :empleado " +
            "AND (n.fechaInicio <= :fin AND n.fechaFin >= :inicio)")
    List<Nomina> findByEmpleadoAndPeriodoSolapado(
            @Param("empleado") Empleado empleado,
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin
    );
    // Para calcular acumulados: nóminas del mismo empleado, en el mismo año, cuya fecha FIN sea anterior a la fecha INICIO de la nómina actual.
    @Query("SELECT n FROM Nomina n WHERE n.empleado.id = :empleadoId AND FUNCTION('YEAR', n.fechaInicio) = :anio AND n.fechaFin < :fechaInicioReferencia")
    List<Nomina> findNominasAnterioresEnAnioConFechaInicioReferencia(
            @Param("empleadoId") UUID empleadoId,
            @Param("anio") int anio,
            @Param("fechaInicioReferencia") LocalDate fechaInicioReferencia
    );

    List<Nomina> findByEmpleadoId(UUID empleadoId);

    Page<Nomina> findAll(Specification<Nomina> nominaSpecification, Pageable pageable);
}
