package org.grupo_h.empleados.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoSimpleConDNIDTO {
    private UUID id;
    private String nombre;
    private String apellidos;
    private String numeroDocumento;

    public String getNombreCompleto() {
        return nombre + " " + apellidos;
    }
}
