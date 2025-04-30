package org.grupo_h.comun.entity.auxiliar;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tipo_tarjeta")
public class TipoTarjetaCredito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    private TipoTarjetaCredito (TipoTarjetaCredito tipoTarjetaCredito) {
        this.id = tipoTarjetaCredito.id;
        this.nombre = tipoTarjetaCredito.nombre;
        this.descripcion = tipoTarjetaCredito.descripcion;
    }

    public static TipoTarjetaCredito of(String nombre, String descripcion) {
        TipoTarjetaCredito nuevaTipoTarjetaCredito = new TipoTarjetaCredito();
        nuevaTipoTarjetaCredito.setNombre(nombre);
        nuevaTipoTarjetaCredito.setDescripcion(descripcion);
        return nuevaTipoTarjetaCredito;
    }


}
