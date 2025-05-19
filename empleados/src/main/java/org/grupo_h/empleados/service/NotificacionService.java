package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.Empleado; // Necesitarás la entidad Empleado
import org.grupo_h.empleados.dto.notificacion.NotificacionDTO; // El DTO que creamos
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificacionService {

    /**
     * Crea una nueva notificación para un empleado.
     * Este método será llamado típicamente por un listener de eventos.
     *
     * @param empleadoDestino     El empleado que recibirá la notificación.
     * @param tipoEvento          Una cadena que describe el tipo de evento (ej. "NUEVA_SOLICITUD_COLABORACION").
     * @param mensaje             El contenido del mensaje de la notificación.
     * @param idReferenciaEntidad (Opcional) El ID de la entidad relacionada con la notificación (ej. ID de una solicitud).
     * @param urlReferencia       (Opcional) Una URL relativa para enlazar al detalle de la entidad relacionada.
     */
    void crearNotificacion(Empleado empleadoDestino, String tipoEvento, String mensaje, UUID idReferenciaEntidad, String urlReferencia);

    /**
     * Obtiene las notificaciones para un empleado específico, paginadas y ordenadas.
     *
     * @param emailEmpleado Email del empleado cuyas notificaciones se quieren obtener.
     * @param pageable      Información de paginación y ordenación.
     * @return Una página de NotificacionDTO.
     * @throws org.grupo_h.comun.exceptions.ResourceNotFoundException si el empleado no se encuentra.
     */
    Page<NotificacionDTO> getNotificacionesParaEmpleado(String emailEmpleado, Pageable pageable);

    /**
     * Cuenta el número de notificaciones no leídas para un empleado.
     *
     * @param emailEmpleado Email del empleado.
     * @return El número de notificaciones no leídas.
     * @throws org.grupo_h.comun.exceptions.ResourceNotFoundException si el empleado no se encuentra.
     */
    long contarNotificacionesNoLeidas(String emailEmpleado);

    /**
     * Marca una notificación específica como leída.
     *
     * @param idNotificacion ID de la notificación a marcar como leída.
     * @param emailEmpleado  Email del empleado que es dueño de la notificación (para seguridad).
     * @throws org.grupo_h.comun.exceptions.ResourceNotFoundException    si la notificación o el empleado no se encuentran.
     * @throws org.springframework.security.access.AccessDeniedException si el empleado no es el dueño de la notificación.
     */
    void marcarNotificacionComoLeida(UUID idNotificacion, String emailEmpleado);

    /**
     * Marca todas las notificaciones no leídas de un empleado como leídas.
     *
     * @param emailEmpleado Email del empleado.
     * @throws org.grupo_h.comun.exceptions.ResourceNotFoundException si el empleado no se encuentra.
     */
    void marcarTodasLasNotificacionesComoLeidas(String emailEmpleado);
}
