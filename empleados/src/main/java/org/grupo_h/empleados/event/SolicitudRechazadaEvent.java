package org.grupo_h.empleados.event;

import lombok.Getter;
import org.grupo_h.comun.entity.chat.SolicitudColaboracion;
import org.springframework.context.ApplicationEvent;

@Getter
public class SolicitudRechazadaEvent extends ApplicationEvent {
    private final SolicitudColaboracion solicitudRechazada;
    private final String motivoRechazo; // Opcional, si se quiere comunicar un motivo

    public SolicitudRechazadaEvent(Object source, SolicitudColaboracion solicitudRechazada, String motivoRechazo) {
        super(source);
        this.solicitudRechazada = solicitudRechazada;
        this.motivoRechazo = motivoRechazo;
    }
}