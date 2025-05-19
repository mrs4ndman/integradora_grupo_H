package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.comun.entity.chat.SolicitudColaboracion;
import org.grupo_h.comun.entity.chat.SolicitudColaboracion.EstadoSolicitud;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SolicitudColaboracionRepository extends JpaRepository<SolicitudColaboracion, UUID> {

    Page<SolicitudColaboracion> findByEmpleadoEmisor(Empleado emisor, Pageable pageable);
    Page<SolicitudColaboracion> findByEmpleadoReceptor(Empleado receptor, Pageable pageable);
    Page<SolicitudColaboracion> findByEmpleadoReceptorAndEstado(Empleado receptor, EstadoSolicitud estadoSolicitud, Pageable pageable);

    // Encontrar solicitudes emitidas por un empleado, paginadas
    Page<SolicitudColaboracion> findByEmpleadoEmisorOrderByFechaSolicitudDesc(Empleado empleadoEmisor, Pageable pageable);

    // Encontrar solicitudes recibidas por un empleado, paginadas
    Page<SolicitudColaboracion> findByEmpleadoReceptorOrderByFechaSolicitudDesc(Empleado empleadoReceptor, Pageable pageable);

    // Encontrar solicitudes pendientes recibidas por un empleado, paginadas (para un conteo o lista específica)
    Page<SolicitudColaboracion> findByEmpleadoReceptorAndEstadoOrderByFechaSolicitudDesc(Empleado empleadoReceptor, EstadoSolicitud estado, Pageable pageable);

    // Contar solicitudes pendientes para un receptor
    long countByEmpleadoReceptorAndEstado(Empleado empleadoReceptor, EstadoSolicitud estado);

    // Encontrar una solicitud pendiente específica entre dos empleados para evitar duplicados
    Optional<SolicitudColaboracion> findByEmpleadoEmisorAndEmpleadoReceptorAndEstado(Empleado empleadoEmisor, Empleado empleadoReceptor, EstadoSolicitud estado);

    // Para tareas de limpieza programadas (ejemplo)
    List<SolicitudColaboracion> findByEstadoAndFechaSolicitudBefore(EstadoSolicitud estado, LocalDateTime fechaLimite);

}