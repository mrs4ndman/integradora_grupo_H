package org.grupo_h.empleados.Validaciones.ValidacionesPersonalizadas;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.grupo_h.empleados.dto.EmpleadoRegistroDTO;

import java.time.LocalDate;
import java.time.Period;

public class EdadCoherenteValidator implements ConstraintValidator<EdadCoherente, EmpleadoRegistroDTO> {
    @Override
    public boolean isValid(EmpleadoRegistroDTO empleadoDTO, ConstraintValidatorContext constraintValidatorContext) {
        if (empleadoDTO.getFechaNacimientoDTO() == null || empleadoDTO.getEdadDTO() == null) {
            return true; // Deja que @NotNull y @Past manejen los nulos
        }

        LocalDate fechaNacimiento = empleadoDTO.getFechaNacimientoDTO();
        int edadCalculada = Period.between(fechaNacimiento, LocalDate.now()).getYears();
        return edadCalculada == empleadoDTO.getEdadDTO();
    }
}
