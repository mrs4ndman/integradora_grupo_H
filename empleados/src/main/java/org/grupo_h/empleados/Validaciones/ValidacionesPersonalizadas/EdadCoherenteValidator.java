package org.grupo_h.empleados.Validaciones.ValidacionesPersonalizadas;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.grupo_h.empleados.dto.EmpleadoRegistroDTO;

import java.time.LocalDate;
import java.time.Period;

public class EdadCoherenteValidator implements ConstraintValidator<EdadCoherente, EmpleadoRegistroDTO> {
    @Override
    public boolean isValid(EmpleadoRegistroDTO empleadoDTO, ConstraintValidatorContext constraintValidatorContext) {
        if (empleadoDTO.getFechaNacimiento() == null || empleadoDTO.getEdad() == null) {
            return true; // Deja que @NotNull y @Past manejen los nulos
        }

        LocalDate fechaNacimiento = empleadoDTO.getFechaNacimiento();
        int edadCalculada = Period.between(fechaNacimiento, LocalDate.now()).getYears();
        return edadCalculada == empleadoDTO.getEdad();
    }
}
