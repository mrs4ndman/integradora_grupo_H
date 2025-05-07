package org.grupo_h.empleados.Validaciones.ValidacionesPersonalizadas;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ImagenEmpleadoValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ImagenValida {
    String message() default "Imagen no válida";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
