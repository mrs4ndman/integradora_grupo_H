package org.grupo_h.empleados.listener;

import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.comun.entity.chat.SolicitudColaboracion;
import org.grupo_h.comun.repository.EmpleadoRepository;
import org.grupo_h.empleados.event.ProductoNuevoEvent;
import org.grupo_h.empleados.event.SolicitudColaboracionRecibidaEvent;
import org.grupo_h.empleados.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificacionEventListener {
    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private EmpleadoRepository empleadoRepository; // Para notificar a todos los empleados

    @EventListener
    public void handleSolicitudColaboracionRecibida(SolicitudColaboracionRecibidaEvent event) {
        SolicitudColaboracion solicitud = event.getSolicitud();
        Empleado destinatario = solicitud.getEmpleadoReceptor();
        String mensaje = "Has recibido una nueva solicitud de colaboración de " + solicitud.getEmpleadoEmisor().getNombreCompleto() + ".";
        String url = "/empleados/colaboraciones/solicitudes/recibidas"; // URL para ver la solicitud
        notificacionService.crearNotificacion(destinatario, "NUEVA_SOLICITUD_COLABORACION", mensaje, solicitud.getId(), url);
    }

    // Ejemplo para cuando se acepta una solicitud
    /*
    @EventListener
    public void handleSolicitudColaboracionAceptada(SolicitudColaboracionAceptadaEvent event) {
        SolicitudColaboracion solicitud = event.getSolicitud();
        // Notificar al emisor de la solicitud que fue aceptada
        Empleado emisorOriginal = solicitud.getEmpleadoEmisor();
        String mensaje = "Tu solicitud de colaboración a " + solicitud.getEmpleadoReceptor().getNombreCompleto() + " ha sido aceptada.";
        String url = "/empleados/colaboraciones/activas/" + solicitud.getColaboracion().getId();
        notificacionService.crearNotificacion(emisorOriginal, "SOLICITUD_ACEPTADA", mensaje, solicitud.getColaboracion().getId(), url);
    }
    */

    @EventListener
    public void handleProductoNuevo(ProductoNuevoEvent event) {
        // Notificar a todos los empleados (o a un grupo específico)
        List<Empleado> todosLosEmpleados = empleadoRepository.findAll(); // O filtrar según roles/departamentos
        String mensaje = "Se ha añadido un nuevo producto al catálogo: " + event.getProducto().getDescripcion() + " por " + event.getNombreQuienLoAgrego() + ".";
        String url = "/empleados/productos/detalle/" + event.getProducto().getId();

        for (Empleado empleado : todosLosEmpleados) {
            // Evitar notificar al que lo agregó si es un empleado
            if (!empleado.getUsuario().getEmail().equalsIgnoreCase(event.getNombreQuienLoAgrego())) {
                notificacionService.crearNotificacion(empleado, "NUEVO_PRODUCTO", mensaje, event.getProducto().getId(), url);
            }
        }
    }
}