package org.grupo_h.comun.entity.auxiliar;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TipoVia {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String tipoVia;
}
