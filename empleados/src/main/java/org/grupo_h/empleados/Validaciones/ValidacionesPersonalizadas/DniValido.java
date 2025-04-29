package org.grupo_h.empleados.Validaciones.ValidacionesPersonalizadas;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DniValidator.class)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DniValido {
    String message() default "DNI no válido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
