package org.grupo_h.empleados.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EdadCoherenteValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EdadCoherente {
    String message() default "{Validacion.edad.fueraRango}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
