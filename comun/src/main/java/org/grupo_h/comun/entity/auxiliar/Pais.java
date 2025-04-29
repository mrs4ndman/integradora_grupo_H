package org.grupo_h.comun.entity.auxiliar;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Pais {
    @Id
    @Column(name = "Cod_pais", length = 2, nullable = false)
    private String codigoPais;
    @Column(name = "Nombre_Pais",
            unique = true)
    private String nombrePais;
    @Column(name = "Prefijo_Pais",
    unique = true)
    private String prefijoPais;

}