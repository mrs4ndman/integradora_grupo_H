package org.grupo_h.comun.entity.auxiliar;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntidadBancaria {

    @Id
    private String codigoEntidadBancaria;
    @Column(name = "Entidad_Bancaria")
    private String nombreEntidadBancaria;

}
