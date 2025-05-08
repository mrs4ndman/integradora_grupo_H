package org.grupo_h.administracion.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.grupo_h.administracion.dto.ErrorResponseDTO;
import org.grupo_h.administracion.dto.ImportacionResponseDTO;
import org.grupo_h.administracion.service.CatalogoService;
// Asumiendo que creaste esta excepción personalizada como se sugirió:
import org.grupo_h.administracion.exception.CatalogoImportValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/administracion/catalogo")
public class CatalogoRestController {

    private static final Logger logger = LoggerFactory.getLogger(CatalogoRestController.class);
    private final CatalogoService catalogoService;

    @Autowired
    public CatalogoRestController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @PostMapping(value = "/importar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importarCatalogo(@RequestParam("archivo") MultipartFile archivo, HttpServletRequest request) {

        if (archivo.isEmpty() || archivo.getOriginalFilename() == null || archivo.getOriginalFilename().trim().isEmpty()) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Petición Incorrecta",
                    "Por favor, seleccione un archivo JSON válido para importar.",
                    null,
                    request.getRequestURI());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Validar que sea un archivo JSON por extensión (validación básica)
        String nombreArchivo = archivo.getOriginalFilename();
        if (!nombreArchivo.toLowerCase().endsWith(".json")) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Tipo de Archivo Incorrecto",
                    "El archivo debe ser de tipo JSON (.json).",
                    null,
                    request.getRequestURI());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }


        try {
            // Idealmente, CatalogoService.importarCatalogo ahora devuelve ImportacionResponseDto
            // o lanza CatalogoImportValidationException.
            // Por ahora, mantendremos la lógica de parseo del mensaje de texto del servicio,
            // pero es mejor refactorizar el servicio.

            String resultadoMsg = catalogoService.importarCatalogo(archivo);
            logger.info("Importación de catálogo API procesada: {}", resultadoMsg);

            // Ejemplo de parseo del mensaje del servicio (MEJORAR ESTO HACIENDO QUE EL SERVICIO DEVUELVA UN DTO)
            int nuevos = 0;
            int actualizados = 0;
            if (resultadoMsg.startsWith("Importación completada")) {
                try {
                    String[] parts = resultadoMsg.split("\\.\\s*");
                    for (String part : parts) {
                        if (part.contains("Productos nuevos:")) {
                            nuevos = Integer.parseInt(part.replaceAll("[^0-9]", ""));
                        } else if (part.contains("Productos actualizados:")) {
                            actualizados = Integer.parseInt(part.replaceAll("[^0-9]", ""));
                        }
                    }
                } catch (NumberFormatException e) {
                    logger.warn("No se pudo parsear completamente el mensaje de éxito del servicio: {}", resultadoMsg);
                }
            }
            ImportacionResponseDTO responseDto = new ImportacionResponseDTO(resultadoMsg, nuevos, actualizados, Collections.emptyList());
            return ResponseEntity.ok(responseDto);

        } catch (CatalogoImportValidationException e) { // Manejo de excepción de validación personalizada
            logger.warn("Errores de validación durante la importación del catálogo: {}", e.getMessage());
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Error de Validación",
                    e.getMessage(), // Mensaje principal de la excepción
                    e.getValidationErrors(), // Lista de errores detallados
                    request.getRequestURI());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) { // Otras excepciones generales
            logger.error("Error API durante la importación del catálogo: {}", e.getMessage(), e);
            String mensajeErrorPrincipal = "Error durante la importación del catálogo.";
            List<String> detallesError = Collections.singletonList(e.getMessage());

            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            String errorTipo = "Error Interno del Servidor";

            if (e.getMessage() != null) {
                if (e.getMessage().contains("Error de sintaxis en el archivo JSON")) {
                    status = HttpStatus.BAD_REQUEST;
                    errorTipo = "Error de Sintaxis JSON";
                    mensajeErrorPrincipal = "El archivo JSON contiene errores de sintaxis.";
                } else if (e.getMessage().contains("Proveedor") && e.getMessage().contains("no encontrado")) {
                    status = HttpStatus.BAD_REQUEST;
                    errorTipo = "Error de Datos";
                    mensajeErrorPrincipal = "Error con el proveedor especificado en el catálogo.";
                }
                // Puedes añadir más condiciones para errores específicos que quieras mapear a BAD_REQUEST
            }

            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    LocalDateTime.now(),
                    status.value(),
                    errorTipo,
                    mensajeErrorPrincipal,
                    detallesError,
                    request.getRequestURI());
            return new ResponseEntity<>(errorResponse, status);
        }
    }
}