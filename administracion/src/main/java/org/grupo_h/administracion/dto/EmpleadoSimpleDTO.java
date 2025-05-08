package org.grupo_h.administracion.dto;

import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoSimpleDTO {
    private UUID id;
    private String nombre;
    private String apellidos;
    private String nombreJefe;
}