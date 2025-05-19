package org.grupo_h.comun.exceptions; // O el paquete de excepciones que prefieras en tu módulo comun

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para indicar que un recurso solicitado no fue encontrado.
 * Al anotarla con @ResponseStatus(HttpStatus.NOT_FOUND), si esta excepción
 * llega al DispatcherServlet sin ser capturada antes (por ejemplo, por un @ControllerAdvice),
 * automáticamente resultará en una respuesta HTTP 404 Not Found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException(String message) {
    super(message);
  }

  public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
    super(String.format("%s no encontrado con %s : '%s'", resourceName, fieldName, fieldValue));
  }

  public ResourceNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}