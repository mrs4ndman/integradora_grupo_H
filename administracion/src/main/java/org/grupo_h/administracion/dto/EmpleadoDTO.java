package org.grupo_h.administracion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoDTO {
    private UUID id;
    private String nombre;
    private String apellidos;
    private Integer edad;
    private String nombreDepartamento;
    private String numeroDni;
    private boolean activo;

    public EmpleadoDTO(UUID id, String nombre, String apellidos, Integer edad, String nombreDept, String numeroDocumento) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.edad = edad;
        this.nombreDepartamento = nombreDept;
        this.numeroDni = numeroDocumento;
    }
    private String fotoBase64; // para mostrar la foto
    private MultipartFile fotografiaDTO;

    public EmpleadoDTO(UUID id, String nombre, String apellidos, Integer edad, String nombreDept, String numeroDocumento, String fotoB64, Object o) {
    }
//    private byte[] fotografiaArchivo;

}
