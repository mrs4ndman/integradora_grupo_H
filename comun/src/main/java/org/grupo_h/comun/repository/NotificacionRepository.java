package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.comun.entity.Notificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, UUID> {

    /**
     * Encuentra todas las notificaciones para un empleado destino, ordenadas por fecha de creación descendente.
     * @param empleadoDestino El empleado al que pertenecen las notificaciones.
     * @param pageable Información de paginación y ordenación.
     * @return Una página de notificaciones.
     */
    Page<Notificacion> findByEmpleadoDestinoOrderByFechaHoraCreacionDesc(Empleado empleadoDestino, Pageable pageable);

    /**
     * Cuenta el número de notificaciones no leídas para un empleado destino.
     * @param empleadoDestino El empleado.
     * @return El número de notificaciones no leídas.
     */
    long countByEmpleadoDestinoAndLeidaFalse(Empleado empleadoDestino);

    /**
     * Marca una notificación específica como leída para un empleado.
     * Se usa @Modifying y @Query para asegurar que la operación se realice y para control granular.
     * @param idNotificacion ID de la notificación a marcar.
     * @param empleado El empleado dueño de la notificación (para seguridad).
     * @return El número de filas actualizadas (debería ser 1 si tuvo éxito).
     */
    @Modifying
    @Query("UPDATE Notificacion n SET n.leida = true WHERE n.id = :idNotificacion AND n.empleadoDestino = :empleadoDestino")
    int marcarComoLeida(@Param("idNotificacion") UUID idNotificacion, @Param("empleadoDestino") Empleado empleado);

    /**
     * Marca todas las notificaciones no leídas de un empleado como leídas.
     * @param empleadoDestino El empleado.
     * @return El número de filas actualizadas.
     */
    @Modifying
    @Query("UPDATE Notificacion n SET n.leida = true WHERE n.empleadoDestino = :empleadoDestino AND n.leida = false")
    int marcarTodasComoLeidasParaEmpleado(@Param("empleadoDestino") Empleado empleadoDestino);

    // Podrías añadir métodos para eliminar notificaciones antiguas si es necesario para tareas de mantenimiento
    // List<Notificacion> findByLeidaTrueAndFechaHoraCreacionBefore(LocalDateTime fechaLimite);
}
