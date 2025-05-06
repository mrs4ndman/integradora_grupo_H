package org.grupo_h.comun.entity.auxiliar;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.grupo_h.comun.entity.Empleado;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
public class TarjetaCredito {

    // Relación con TipoTarjeta
    @ManyToOne
    @JoinColumn(name = "tipo_tarjeta_id", nullable = false)
    private TipoTarjetaCredito tipoTarjetaCredito;

    @Column(name = "numero_tarjeta", nullable = false)
    private String numeroTarjetaCredito;

    @Column(name = "mes_caducidad")
    private String mesCaducidad;

    @Column(name = "anio_caducidad")
    private String anioCaducidad;

    @Column(name = "cvc", length = 3, nullable = false)
    private String cvc;

}
