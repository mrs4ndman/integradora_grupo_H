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

    @Column(name = "Cod_Tipo_Tarjeta",
            unique = true)
    private String codigoTipoTarjetaCredito;

    @Column(name = "Tipo_Tarjeta",
            unique = true)
    private String tipoTarjetaCredito;

    private TipoTarjetaCredito (TipoTarjetaCredito tipoTarjetaCredito) {
        this.id = tipoTarjetaCredito.id;
        this.codigoTipoTarjetaCredito = tipoTarjetaCredito.codigoTipoTarjetaCredito;
        this.tipoTarjetaCredito = tipoTarjetaCredito.tipoTarjetaCredito;
    }

    public static TipoTarjetaCredito of(String tipoTarjetaCredito, String codigoTipoTarjetaCredito) {
        TipoTarjetaCredito nuevaTipoTarjetaCredito = new TipoTarjetaCredito();
        nuevaTipoTarjetaCredito.setCodigoTipoTarjetaCredito(codigoTipoTarjetaCredito);
        nuevaTipoTarjetaCredito.setTipoTarjetaCredito(tipoTarjetaCredito);
        return nuevaTipoTarjetaCredito;
    }


}
