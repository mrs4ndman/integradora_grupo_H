package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.chat.Colaboracion;
import org.grupo_h.comun.entity.chat.Mensaje;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// import java.util.List;
import java.util.UUID;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, UUID> {

    // Encontrar mensajes de una colaboración, paginados y ordenados por fecha de emisión ascendente (los más antiguos primero)
    Page<Mensaje> findByColaboracionOrderByFechaEmisionAsc(Colaboracion colaboracion, Pageable pageable);

    // Encontrar mensajes de una colaboración, paginados y ordenados por fecha de emisión descendente (los más nuevos primero)
    Page<Mensaje> findByColaboracionOrderByFechaEmisionDesc(Colaboracion colaboracion, Pageable pageable);

    // Si necesitas obtener el último mensaje de una colaboración (ejemplo con JPQL)
    // @Query("SELECT m FROM Mensaje m WHERE m.colaboracion = :colaboracion ORDER BY m.fechaEmision DESC LIMIT 1")
    // Optional<Mensaje> findUltimoMensajePorColaboracion(@Param("colaboracion") Colaboracion colaboracion);
    // Nota: LIMIT 1 puede no ser estándar en JPQL para todas las BD, una alternativa es usar Pageable con size 1
    Page<Mensaje> findFirstByColaboracionOrderByFechaEmisionDesc(Colaboracion colaboracion, Pageable pageable); // Usar con PageRequest.of(0,1)
}