package org.grupo_h.administracion.controller;

import org.grupo_h.administracion.dto.CategoriaSimpleDTO;
import org.grupo_h.administracion.dto.ProductoCriteriosBusquedaDTO;
import org.grupo_h.administracion.dto.ProductoResultadoDTO;
// Si necesitas devolver listas de categorías/proveedores para que un frontend construya filtros:
// import org.grupo_h.administracion.dto.CategoriaDTO;
// import org.grupo_h.administracion.dto.ProveedorDTO;
import org.grupo_h.administracion.dto.ProveedorSimpleDTO;
import org.grupo_h.administracion.service.ProductoService;
// import org.grupo_h.administracion.service.CategoriaService; // Si lo implementas
// import org.grupo_h.administracion.service.ProveedorService; // Si lo implementas

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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

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
     * @param descripcion        Descripcion del producto (búsqueda parcial).
     * @param categoriaId   ID de la categoría.
     * @param precioMin     Precio mínimo.
     * @param precioMax     Precio máximo.
     * @param proveedorId   ID del proveedor.
     * @param esPerecedero Estado de perecedero (true/false).
     * @param page          Número de página (por defecto 0).
     * @param size          Tamaño de la página (por defecto 10).
     * @return ResponseEntity
     */
    @GetMapping
    public ResponseEntity<?> buscarProductos(
            // Parámetros de búsqueda del DTO
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) UUID categoriaId,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax,
            @RequestParam(required = false) UUID proveedorId,
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
                descripcion, categoriaId, precioMin, precioMax, proveedorId, esPerecedero
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

    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaSimpleDTO>> obtenerCategoriasParaFiltro() {
        List<CategoriaSimpleDTO> categorias = productoService.listarTodasLasCategorias();
        return ResponseEntity.ok(categorias);
    }
    @GetMapping("/proveedores")
    public ResponseEntity<List<ProveedorSimpleDTO>> obtenerProveedoresParaFiltro() {
        List<ProveedorSimpleDTO> proveedores = productoService.listarTodosLosProveedores();
        return ResponseEntity.ok(proveedores);
    }
}