package org.grupo_h.administracion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoDTO {
    private UUID id;
    private String nombre;
    private String apellido;
    private Integer edad;
    private String nombreDepartamento;
    private String numeroDni;
}
