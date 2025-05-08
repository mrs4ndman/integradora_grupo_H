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
@Table(name = "tipo_tarjeta_credito")
public class TipoTarjetaCredito {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nombre_tipo_tarjeta", nullable = false, unique = true)
    private String nombreTipoTarjeta;

    private TipoTarjetaCredito (TipoTarjetaCredito tipoTarjetaCredito) {
        this.id = tipoTarjetaCredito.id;
        this.nombreTipoTarjeta = tipoTarjetaCredito.nombreTipoTarjeta;
    }

    public static TipoTarjetaCredito of(String nombre) {
        TipoTarjetaCredito nuevaTipoTarjetaCredito = new TipoTarjetaCredito();
        nuevaTipoTarjetaCredito.setNombreTipoTarjeta(nombre);
        return nuevaTipoTarjetaCredito;
    }


}
