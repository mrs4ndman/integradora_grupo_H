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
public class TipoDocumento {
    @Id
    @Column(name = "Cod_Tipo_Doc", length = 3, nullable = false)
    private String codTipoDocumento;
    private String tipoDocumento;
}
