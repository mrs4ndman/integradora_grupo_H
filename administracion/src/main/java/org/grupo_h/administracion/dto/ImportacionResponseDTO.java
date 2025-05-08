package org.grupo_h.administracion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportacionResponseDTO {
    private String mensaje;
    private int productosNuevos;
    private int productosActualizados;
    private List<String> erroresIndividuales; // Para reportar problemas menores si decides no hacer la importación totalmente atómica para ciertos warnings
}
