package org.grupo_h.empleados.event;

import lombok.Getter;
import org.grupo_h.comun.entity.chat.SolicitudColaboracion;
import org.springframework.context.ApplicationEvent;

@Getter
public class SolicitudColaboracionRecibidaEvent extends ApplicationEvent {
    private final SolicitudColaboracion solicitud;

    public SolicitudColaboracionRecibidaEvent(Object source, SolicitudColaboracion solicitud) {
        super(source);
        this.solicitud = solicitud;
    }
}
