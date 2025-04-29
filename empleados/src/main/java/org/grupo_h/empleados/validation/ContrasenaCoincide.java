package org.grupo_h.empleados.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Anotación para validar que los campos de contraseña y confirmación coinciden en un DTO.
 */
@Target({TYPE, ANNOTATION_TYPE}) // Se aplica a nivel de clase
@Retention(RUNTIME)
@Constraint(validatedBy = ContrasenaCoincideValidator.class) // Validador que implementa la lógica
@Documented
public @interface ContrasenaCoincide {
    String message() default "Las contraseñas no coinciden"; // Mensaje de error por defecto
    Class<?>[] groups() default {}; // Grupos de validación (raramente necesario aquí)
    Class<? extends Payload>[] payload() default {}; // Metadatos adicionales (raramente necesario)
}
