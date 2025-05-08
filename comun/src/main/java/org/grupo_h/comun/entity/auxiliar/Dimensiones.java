package org.grupo_h.comun.entity.auxiliar;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Dimensiones {
    private Double ancho;
    private Double profundo;
    private Double alto;
}
