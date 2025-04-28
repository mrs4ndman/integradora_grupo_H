package org.grupo_h.empleados.Validaciones.ValidacionesPersonalizadas;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class MinimoDosCheckboxesValidator
        implements ConstraintValidator<MinimoDosCheckbox, List<String>> {

    @Override
    public boolean isValid(List<String> especialidadesSeleccionadas,
                           ConstraintValidatorContext context) {
        if (especialidadesSeleccionadas == null) {
            return false;
        }
        return especialidadesSeleccionadas.size() >= 2; // Al menos 2 elementos
    }
}
