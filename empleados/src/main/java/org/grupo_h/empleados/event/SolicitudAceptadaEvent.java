package org.grupo_h.empleados.event;

import lombok.Getter;
import org.grupo_h.comun.entity.chat.SolicitudColaboracion;
import org.springframework.context.ApplicationEvent;

@Getter
public class SolicitudAceptadaEvent extends ApplicationEvent {
    private final SolicitudColaboracion solicitudAceptada;

    public SolicitudAceptadaEvent(Object source, SolicitudColaboracion solicitudAceptada) {
        super(source);
        this.solicitudAceptada = solicitudAceptada;
    }
}
