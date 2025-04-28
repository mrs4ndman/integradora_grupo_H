package org.grupo_h.empleados.Validaciones.ValidacionesPersonalizadas;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MinimoDosCheckboxesValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MinimoDosCheckbox {
    String message() default "Debes seleccionar al menos 2 opciones";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
