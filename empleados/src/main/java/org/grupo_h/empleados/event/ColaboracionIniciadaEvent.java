package org.grupo_h.empleados.event;

import lombok.Getter;
import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.comun.entity.chat.Colaboracion;
import org.springframework.context.ApplicationEvent;

@Getter
public class ColaboracionIniciadaEvent extends ApplicationEvent {
    private final Colaboracion colaboracion;
    private final Empleado empleadoQueAcepto; // El empleado que aceptó la solicitud e inició formalmente la colaboración


    public ColaboracionIniciadaEvent(Object source, Colaboracion colaboracion, Empleado empleadoQueAcepto) {
        super(source);
        this.colaboracion = colaboracion;
        this.empleadoQueAcepto = empleadoQueAcepto;
    }
}

