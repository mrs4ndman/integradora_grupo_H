package org.grupo_h.empleados.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartamentoDTO {

    private UUID id;

    private String codigo;

    private String nombreDept;
}
