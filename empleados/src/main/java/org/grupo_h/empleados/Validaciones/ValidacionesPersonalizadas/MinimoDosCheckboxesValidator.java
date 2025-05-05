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
        return especialidadesSeleccionadas.size() >= 2; // Al menos 2 elementos
    }
}
