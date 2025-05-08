package org.grupo_h.administracion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {
    private LocalDateTime timestamp;
    private int status;
    private String error; // ej. "Bad Request", "Internal Server Error"
    private String mensaje; // Mensaje principal del error
    private List<String> detalles; // Lista de errores específicos (ej. errores de validación)
    private String path; // Ruta que originó el error
}