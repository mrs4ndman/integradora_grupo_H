package org.grupo_h.empleados.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.grupo_h.empleados.dto.ReseteoContrasenaDTO;
import org.grupo_h.empleados.dto.UsuarioRegistroDTO;

public class ContrasenaCoincideValidator implements ConstraintValidator<ContrasenaCoincide, Object> {

    @Override
    public void initialize(ContrasenaCoincide constraintAnnotation) {
    }

    @Override
    // El objeto 'obj' puede ser UsuarioRegistroDTO o PasswordResetDTO
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        String password = null;
        String confirmPassword = null;
        String fieldToAnnotateError = "confirmPassword";

        // Comprobar el tipo de objeto y extraer las contraseñas
        if (obj instanceof UsuarioRegistroDTO) {
            UsuarioRegistroDTO dto = (UsuarioRegistroDTO) obj;
            password = dto.getContrasena();
            confirmPassword = dto.getConfirmarContrasena();
            fieldToAnnotateError = "confirmarContrasena";
        } else if (obj instanceof ReseteoContrasenaDTO) {
            ReseteoContrasenaDTO dto = (ReseteoContrasenaDTO) obj;
            password = dto.getNuevaContraseña();
            confirmPassword = dto.getConfirmarNuevaContraseña();
            fieldToAnnotateError = "confirmarNuevaContraseña";
        } else {
            return true;
        }

        // La lógica de comparación
        boolean passwordsMatch = password != null && password.equals(confirmPassword);

        if (!passwordsMatch) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode(fieldToAnnotateError) // Usa el nombre de campo correcto
                    .addConstraintViolation();
        }

        return passwordsMatch;
    }
}
