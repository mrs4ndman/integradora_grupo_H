package org.grupo_h.empleados.dto.colaboracion;

import lombok.Data;
import org.grupo_h.comun.entity.chat.Colaboracion.EstadoColaboracion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class ColaboracionResponseDTO {
    private UUID id;

    private UUID idEmpleadoA;
    private String nombreEmpleadoA;
    private String emailEmpleadoA;

    private UUID idEmpleadoB;
    private String nombreEmpleadoB;
    private String emailEmpleadoB;

    private EstadoColaboracion estado;

    // Info del periodo actual o todos los periodos
    private List<PeriodoColaboracionDTO> periodos;

    // Fechas relevantes de la colaboracion
    private LocalDateTime fechaBloqueoRechazo;
    private LocalDateTime fechaBloqueoCancelacion;
}
