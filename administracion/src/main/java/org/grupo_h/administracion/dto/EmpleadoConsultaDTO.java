package org.grupo_h.administracion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
public class EmpleadoConsultaDTO {

    private String nombreDTO;

    private String numeroDni;

    private String apellidosDTO;

    private UUID departamentoDTO;

    private LocalDate fechaNacimientoDTO;

    private Integer edadMin;
    private Integer edadMax;
}
