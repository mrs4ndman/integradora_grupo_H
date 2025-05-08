package org.grupo_h.empleados.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Data // Genera getters, setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
public class EtiquetaDTO {
    private UUID id;
    private String nombre;
}
