package org.grupo_h.empleados.Validaciones.ValidacionesPersonalizadas;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.grupo_h.empleados.dto.EspecialidadesEmpleadoDTO;

import java.util.List;

public class MinimoDosCheckboxesValidator
        implements ConstraintValidator<MinimoDosCheckbox, List<EspecialidadesEmpleadoDTO>> {

    @Override
    public boolean isValid(List<EspecialidadesEmpleadoDTO> especialidadesSeleccionadas,
                           ConstraintValidatorContext context) {
        if (especialidadesSeleccionadas == null) {
            return false;
        }
        long count = especialidadesSeleccionadas.stream()
                .filter(EspecialidadesEmpleadoDTO::isSeleccionada)
                .count();
        return count >= 2;
    }
}
