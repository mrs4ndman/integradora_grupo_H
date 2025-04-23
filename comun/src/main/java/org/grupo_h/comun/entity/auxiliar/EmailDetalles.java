package org.grupo_h.comun.entity.auxiliar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDetalles {
    private String destinatario;
    private String cuerpoMsg;
    private String asunto;
}
