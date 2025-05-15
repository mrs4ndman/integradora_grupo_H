package org.grupo_h.empleados.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartamentoDTO {

    private UUID id;

    @NotBlank
    private String codigo;

    @NotBlank
    private String nombreDept;

    // Constructor para crear departamentos
    public DepartamentoDTO(String codigo, String nombreDept) {
        this.codigo = codigo;
        this.nombreDept = nombreDept;
    }

    public String toStringBonito() {
        return "Nombre: " +  nombreDept +  " | Codigo: "  + codigo;
    }
}
