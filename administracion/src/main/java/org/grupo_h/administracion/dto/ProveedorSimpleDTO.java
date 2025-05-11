package org.grupo_h.administracion.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorSimpleDTO {
    private UUID id;
    private String nombre;
}