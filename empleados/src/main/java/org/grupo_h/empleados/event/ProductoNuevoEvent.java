package org.grupo_h.empleados.event;

import lombok.Getter;
import org.grupo_h.comun.entity.Producto;
import org.springframework.context.ApplicationEvent;

@Getter
public class ProductoNuevoEvent extends ApplicationEvent {
    private final Producto producto; // TODO: Posible cambio a ProductoDTO
    private final String nombreQuienLoAgrego;

    public ProductoNuevoEvent(Object source, Producto producto, String nombreQuienLoAgrego) {
        super(source);
        this.producto = producto;
        this.nombreQuienLoAgrego = nombreQuienLoAgrego;
    }
}
