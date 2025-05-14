package org.grupo_h.administracion.controller;

import jakarta.validation.Valid;
import org.grupo_h.administracion.dto.*;
import org.grupo_h.administracion.service.ProductoService;

import org.grupo_h.comun.entity.Producto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/administrador/productos")
public class ProductoRestController {

    private final ProductoService productoService;
    private static final Logger logger = LoggerFactory.getLogger(ProductoRestController.class);


    private static final List<String> SORT_VALIDOS = Arrays.asList(
            "id",
            "descripcion",
            "precio",
            "marca",
            "categoria.nombre",
            "proveedor.nombre",
            "valoracion",
            "unidades",
            "esPerecedero",
            "fechaAlta"
    );

    @Autowired
    public ProductoRestController(ProductoService productoService
                                 /*, CategoriaService categoriaService,
                                    ProveedorService proveedorService */) {
        this.productoService = productoService;
        // this.categoriaService = categoriaService;
        // this.proveedorService = proveedorService;
    }

    /**
     * Endpoint REST para buscar productos de forma paginada y filtrada.
     * Parámetros de búsqueda se pasan como query params.
     *
     * @param descripcion  Descripcion del producto (búsqueda parcial).
     * @param categoriaId  ID de la categoría.
     * @param precioMin    Precio mínimo.
     * @param precioMax    Precio máximo.
     * @param proveedorIds  ID del proveedor.
     * @param esPerecedero Estado de perecedero (true/false).
     * @param page         Número de página (por defecto 0).
     * @param size         Tamaño de la página (por defecto 10).
     * @return ResponseEntity
     */
    @GetMapping
    public ResponseEntity<?> buscarProductos(
            // Parámetros de búsqueda del DTO
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) UUID categoriaId,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax,
            @RequestParam(required = false) List<UUID> proveedorIds,
            @RequestParam(required = false) Boolean esPerecedero,
            // Parámetros de paginación
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            // Parámetro de ordenación específico para la API
            @RequestParam(name = "sort", defaultValue = "descripcion,asc", required = false) String sortParam,
            // Ignorar sortField y sortDir si llegan a este endpoint de API
            @RequestParam(required = false) String sortField_ignored,
            @RequestParam(required = false) String sortDir_ignored
    ) {

        logger.info(">>>> CONTROLADOR API: ProductoRestController.buscarProductos - INICIO <<<<");
        logger.info("[API Controller] Petición entrante (sortParam): {}", sortParam);

        ProductoCriteriosBusquedaDTO criterios = new ProductoCriteriosBusquedaDTO(
                descripcion, categoriaId, precioMin, precioMax, proveedorIds, esPerecedero
        );

        List<Sort.Order> orders = new ArrayList<>();
        if (StringUtils.hasText(sortParam)) { // Usar StringUtils.hasText para verificar que no sea nulo, vacío o solo espacios
            String[] parts = sortParam.split(",", 2);
            String field = parts[0].trim().toLowerCase();

            if (StringUtils.hasText(field) && SORT_VALIDOS.contains(field)) {
                Sort.Direction direction = Sort.Direction.ASC; // Por defecto ASC
                if (parts.length == 2 && StringUtils.hasText(parts[1]) && parts[1].trim().equalsIgnoreCase("desc")) {
                    direction = Sort.Direction.DESC;
                }
                orders.add(new Sort.Order(direction, field));
                logger.debug("[API Controller] Orden añadida: {} {}", field, direction);
            } else {
                logger.warn("[API Controller] Campo de ordenación no válido o no permitido: '{}' en el parámetro 'sort' con valor '{}'. Se ignora esta orden.", field, sortParam);
            }
        }

        if (orders.isEmpty()) {
            logger.info("[API Controller] No se especificaron órdenes de API válidas o ninguna orden. Aplicando orden por defecto: descripcion,asc");
            orders.add(new Sort.Order(Sort.Direction.ASC, "descripcion"));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));
        logger.debug("[API Controller] Pageable para API: {}", pageable);

        try {
            Page<ProductoResultadoDTO> paginaProductos = productoService.buscarProductos(criterios, pageable);
            return ResponseEntity.ok(paginaProductos);
        } catch (PropertyReferenceException e) {
            logger.error("[API Controller] PropertyReferenceException al intentar ordenar por API: {}", e.getMessage());
            orders.clear();
            orders.add(new Sort.Order(Sort.Direction.ASC, "descripcion"));
            pageable = PageRequest.of(page, size, Sort.by(orders));
            Page<ProductoResultadoDTO> paginaProductos = productoService.buscarProductos(criterios, pageable);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error en el parámetro de ordenación. Se usó orden por defecto. Detalle: " + e.getMessage());
        } catch (Exception e) {
            logger.error("[API Controller] Error interno al procesar la búsqueda de productos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al procesar la búsqueda de productos. Causa: " + e.getMessage());
        }
    }

    /**
     * Obtiene todas las categorías disponibles para usar en filtros de búsqueda.
     *
     * @return ResponseEntity con la lista de categorías simplificadas.
     */
    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaSimpleDTO>> obtenerCategoriasParaFiltro() {
        List<CategoriaSimpleDTO> categorias = productoService.listarTodasLasCategorias();
        return ResponseEntity.ok(categorias);
    }

    /**
     * Obtiene todos los proveedores disponibles para usar en filtros de búsqueda.
     *
     * @return ResponseEntity con la lista de proveedores simplificados.
     */
    @GetMapping("/proveedores")
    public ResponseEntity<List<ProveedorSimpleDTO>> obtenerProveedoresParaFiltro() {
        List<ProveedorSimpleDTO> proveedores = productoService.listarTodosLosProveedores();
        return ResponseEntity.ok(proveedores);
    }

    /**
     * Elimina un producto específico por su ID.
     *
     * @param id ID único del producto a eliminar.
     * @return ResponseEntity con un mensaje de éxito o error.
     */
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<Map<String, Object>> eliminarProducto(@PathVariable UUID id) {
        logger.info("Recibida solicitud DELETE /api/productos/{}", id);
        Map<String, Object> response = new HashMap<>();
        try {
            productoService.eliminarProductoPorId(id);
            response.put("message", "Producto con ID " + id + " eliminado correctamente.");
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (Exception e) { // Considera capturar excepciones más específicas si las defines
            logger.error("Error al eliminar producto con ID {}: {}", id, e.getMessage());
            response.put("message", "Error al eliminar el producto: " + e.getMessage());
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            // Podrías tener una excepción ProductoNoEncontradoException y devolver NOT_FOUND
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Endpoint para eliminar todos los productos de una categoría específica.
     *
     * @param categoriaId El ID de la categoría.
     * @return ResponseEntity con el resultado de la operación.
     */
    @DeleteMapping("/borrar/categoria/{categoriaId}")
    public ResponseEntity<Map<String, Object>> eliminarProductosPorCategoria(@PathVariable UUID categoriaId) {
        logger.info("Recibida solicitud DELETE /api/productos/categoria/{}", categoriaId);
        Map<String, Object> response = new HashMap<>();
        try {
            int eliminados = productoService.eliminarProductosPorCategoria(categoriaId);
            response.put("message", "Se eliminaron " + eliminados + " productos de la categoría ID " + categoriaId + ".");
            response.put("eliminados", eliminados);
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al eliminar productos de la categoría ID {}: {}", categoriaId, e.getMessage());
            response.put("message", "Error al eliminar productos por categoría: " + e.getMessage());
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Endpoint para eliminar TODOS los productos.
     *
     * @return ResponseEntity con el resultado de la operación.
     */
    @DeleteMapping("/borrar/todos")
    public ResponseEntity<Map<String, Object>> eliminarTodosLosProductos() {
        logger.info("Recibida solicitud DELETE /api/productos (eliminar todos)");
        Map<String, Object> response = new HashMap<>();
        try {
            productoService.eliminarTodosLosProductos();
            response.put("message", "Todos los productos han sido eliminados correctamente.");
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al eliminar todos los productos: {}", e.getMessage());
            response.put("message", "Error al eliminar todos los productos: " + e.getMessage());
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PatchMapping("/{id}") // O @PutMapping
    public ResponseEntity<?> actualizarProducto(@PathVariable UUID id,
                                                @Valid @RequestBody ProductoModificacionDTO productoModificacionDTO) { // AÑADIDO @Valid
        logger.info("Solicitud PATCH para actualizar producto con ID: {}. Datos: {}", id, productoModificacionDTO);
        Optional<ProductoResultadoDTO> productoActualizadoDTO = productoService.modificarProducto(id, productoModificacionDTO);

        if (productoActualizadoDTO.isPresent()) {
            return ResponseEntity.ok(productoActualizadoDTO.get());
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Producto con ID " + id + " no encontrado para modificar.");
            response.put("status", HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        // No es necesario un try-catch aquí si los @ExceptionHandler globales o locales manejan las excepciones.
    }

    /**
     * Manejador para errores de validación de Beans (anotaciones como @Min, @Size, @DecimalMin).
     * Se activa cuando @Valid falla.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        logger.warn("Errores de validación de datos: {}", errors);
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Manejador para errores cuando el cuerpo de la petición no puede ser leído/convertido por Jackson
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        logger.warn("Error al leer/convertir el cuerpo de la petición HTTP: {}", ex.getMessage());
        Map<String, String> response = new HashMap<>();
        // Intentar dar un mensaje más específico si es posible
        Throwable cause = ex.getCause();
        if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException) {
            com.fasterxml.jackson.databind.exc.InvalidFormatException ife = (com.fasterxml.jackson.databind.exc.InvalidFormatException) cause;
            // Obtener el nombre del campo que causó el error de formato
            String fieldName = ife.getPath().stream()
                    .map(com.fasterxml.jackson.databind.JsonMappingException.Reference::getFieldName)
                    .filter(name -> name != null)
                    .collect(Collectors.joining("."));

            if (fieldName != null && !fieldName.isEmpty()) {
                response.put(fieldName, "El valor proporcionado ('" + ife.getValue() + "') no tiene el formato numérico esperado para el campo '" + fieldName + "'.");
            } else {
                response.put("requestBody", "Hay un problema con el formato de los datos enviados. Verifique los campos numéricos.");
            }
        } else if (cause instanceof com.fasterxml.jackson.core.JsonParseException) {
            response.put("requestBody", "El formato del JSON enviado es inválido.");
        }
        else {
            response.put("requestBody", "No se pudo procesar la petición debido a un formato de datos incorrecto.");
        }
        return ResponseEntity.badRequest().body(response);
    }
}