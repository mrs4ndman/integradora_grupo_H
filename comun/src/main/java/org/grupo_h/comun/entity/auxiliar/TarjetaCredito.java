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
@AllArgsConstructor
public class TarjetaCredito {

    @Column(name = "tipo_tarjeta_id" )
    private Long tipoTarjetaId;

    @Column(name = "numero_tarjeta")
    private String numeroTarjeta;

    @Column(name = "mes_caducidad")
    private String mesCaducidad;

    @Column(name = "anio_caducidad")
    private String anioCaducidad;

    @Column(name = "cvc")
    private String cvc;

    @Transient
    private TipoTarjetaCredito tipo; // Solo para lógica de negocio

}
