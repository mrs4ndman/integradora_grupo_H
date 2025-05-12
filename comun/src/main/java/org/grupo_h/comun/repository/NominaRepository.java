package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.comun.entity.Nomina;
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
}
