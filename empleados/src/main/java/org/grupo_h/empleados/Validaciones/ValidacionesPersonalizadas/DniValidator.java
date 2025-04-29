package org.grupo_h.empleados.Validaciones.ValidacionesPersonalizadas;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DniValidator implements ConstraintValidator<DniValido, String> {
    private static final String DNI_REGEX = "^\\d{8}[A-HJ-NP-TV-Z]$";
    private static final String LETTERS = "TRWAGMYFPDXBNJZSQVHLCKE";

    @Override
    public void initialize(DniValido constraintAnnotation) {
    }

    @Override
    public boolean isValid(String dni, ConstraintValidatorContext context) {
        if (dni == null || dni.isEmpty()) {
            return false;
        }

        // Verificar formato básico
        if (!dni.toUpperCase().matches(DNI_REGEX)) {
            return false;
        }

        // Extraer número y letra
        String cadenaDni = dni.substring(0, 8);
        char Letra = dni.charAt(8);

        try {
            // Parsea a número
            int dniNumber = Integer.parseInt(cadenaDni);
            int remainder = dniNumber % 23;
            char calculatedLetter = LETTERS.charAt(remainder);

            return calculatedLetter == Character.toUpperCase(Letra);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
