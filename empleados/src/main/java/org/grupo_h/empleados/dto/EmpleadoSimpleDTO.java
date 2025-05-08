package org.grupo_h.empleados.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoSimpleDTO {
    private UUID id;
    private String nombreCompleto;

    public EmpleadoSimpleDTO(UUID id, String nombre, String apellido) {
        this.id = id;
        this.nombreCompleto = nombre + " " + apellido;
    }
}
