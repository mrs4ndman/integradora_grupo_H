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
public class TipoTarjetaCredito {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "Tipo_Tarjeta",
            unique = true)
    private String tipoTarjetaCredito;

    private TipoTarjetaCredito (TipoTarjetaCredito tipoTarjetaCredito) {
        this.id = tipoTarjetaCredito.id;
        this.tipoTarjetaCredito = tipoTarjetaCredito.tipoTarjetaCredito;
    }

    public static TipoTarjetaCredito of(String tipoTarjetaCredito) {
        TipoTarjetaCredito nuevaTipoTarjetaCredito = new TipoTarjetaCredito();
        nuevaTipoTarjetaCredito.setTipoTarjetaCredito(tipoTarjetaCredito);
        return nuevaTipoTarjetaCredito;
    }


}
