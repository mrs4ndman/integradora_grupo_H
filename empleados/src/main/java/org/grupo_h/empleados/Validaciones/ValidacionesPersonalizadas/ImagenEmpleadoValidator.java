package org.grupo_h.empleados.Validaciones.ValidacionesPersonalizadas;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

public class ImagenEmpleadoValidator implements ConstraintValidator<ImagenValida, MultipartFile> {

    private final List<String> formatosAptos = Arrays.asList("image/gif", "image/jpeg", "image/jpg");

    private final long MAX_SIZE = 200 * 1024; // 200 KB


    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("La imagen es obligatoria").addConstraintViolation();
            return false;
        }

        if (!formatosAptos.contains(file.getContentType())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Formato de imagen no válido. Solo gif, jpg y jpeg ").addConstraintViolation();
            return false;
        }

        if (file.getSize() > MAX_SIZE) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("La imagen no debe superar los 200 KB").addConstraintViolation();
            return false;
        }

        return true;
    }
}
