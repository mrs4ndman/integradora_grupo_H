// En el módulo administracion, paquete exception
package org.grupo_h.administracion.exception;

import java.util.List;

public class CatalogoImportValidationException extends RuntimeException {
    private List<String> validationErrors;

    public CatalogoImportValidationException(String message, List<String> validationErrors) {
        super(message);
        this.validationErrors = validationErrors;
    }

    public CatalogoImportValidationException(List<String> validationErrors) {
        super("Errores de validación en los productos."); // Mensaje por defecto
        this.validationErrors = validationErrors;
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }
}