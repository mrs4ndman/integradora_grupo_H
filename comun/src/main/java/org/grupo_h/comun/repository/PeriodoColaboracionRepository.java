package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.chat.Colaboracion;
import org.grupo_h.comun.entity.chat.PeriodoColaboracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PeriodoColaboracionRepository extends JpaRepository<PeriodoColaboracion, UUID> {

    // Encontrar todos los periodos de una colaboración específica, ordenados por fecha de inicio
    List<PeriodoColaboracion> findByColaboracionOrderByFechaInicioDesc(Colaboracion colaboracion);

    // Encontrar el periodo activo (sin fecha de fin) para una colaboración
    Optional<PeriodoColaboracion> findByColaboracionAndFechaFinIsNull(Colaboracion colaboracion);
}