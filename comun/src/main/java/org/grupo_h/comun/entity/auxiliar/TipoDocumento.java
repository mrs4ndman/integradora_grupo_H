package org.grupo_h.comun.entity.auxiliar;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class TipoDocumento {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "Cod_Tipo_Doc", length = 3)
    private String tipoDocumento;


}
