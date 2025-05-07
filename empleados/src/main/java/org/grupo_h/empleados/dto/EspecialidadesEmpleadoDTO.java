package org.grupo_h.empleados.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.empleados.Validaciones.GruposValidaciones.DatosDepartamento;
import org.grupo_h.empleados.Validaciones.ValidacionesPersonalizadas.MinimoDosCheckbox;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EspecialidadesEmpleadoDTO {
    @NotNull(groups = DatosDepartamento.class)
    private UUID id;


    private String nombreEspecialidad;

    private Boolean seleccionada = false;

}
