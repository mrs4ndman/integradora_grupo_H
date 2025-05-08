package org.grupo_h.comun.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.grupo_h.comun.entity.auxiliar.TipoDocumento;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DocumentoDNI {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "tipo_documento_cod_tipo_doc")
    private TipoDocumento TipoDocumento;

    private String numeroDocumento;
}
