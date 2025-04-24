package org.grupo_h.comun.entity.auxiliar;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.grupo_h.comun.entity.Empleado;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarjetaCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "tipo_tipo")
    private TipoTarjetaCredito tipo;

    @Column(length = 20)
    private String numeroTarjeta;

    @Column(name = "Mes_Caducidad")
    private String MesCaducidad;

    @Column(name = "Año_Caducidad")
    private String anioCaducidad;

    @Column(name = "CVC")
    private String cvc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

}
