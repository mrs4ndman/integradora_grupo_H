package org.grupo_h.empleados.event;

import lombok.Getter;
import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.comun.entity.chat.Colaboracion;
import org.springframework.context.ApplicationEvent;

@Getter
public class ColaboracionCanceladaEvent extends ApplicationEvent {
    private final Colaboracion colaboracionCancelada;
    private final Empleado empleadoQueCancelo;

    public ColaboracionCanceladaEvent(Object source, Colaboracion colaboracionCancelada, Empleado empleadoQueCancelo) {
        super(source);
        this.colaboracionCancelada = colaboracionCancelada;
        this.empleadoQueCancelo = empleadoQueCancelo;
    }
}