package org.grupo_h.administracion.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.grupo_h.administracion.auxiliar.RestPage;
import org.grupo_h.administracion.dto.*;
import org.grupo_h.administracion.service.ProductoService;
import org.grupo_h.comun.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador para la gestión de productos en el sistema.
 * Proporciona endpoints para la visualización y manejo del catálogo de productos.
 */
@Controller
@RequestMapping("/administrador")
public class GestionProductosController {

    private static final Logger logger = LoggerFactory.getLogger(AdministradorController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProductoService productoService;

    /* ------------------------ GESTION DE PRODUCTOS ----------------------------- */

    /**
     * Muestra la vista para importar un catálogo de productos.
     *
     * @param session            Sesión HTTP del usuario
     * @param redirectAttributes Atributos para redirección en caso de error
     * @return Vista de importación de catálogo o redirección a inicio de sesión si no hay autenticación
     */
    @GetMapping("/importar-catalogo")
    public String vistaImportarCatalogo(HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("emailAutenticadoAdmin") == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para acceder a esta página.");
            return "redirect:/administrador/inicio-sesion";
        }
        return "importarCatalogo";
    }

    /**
     * Muestra la vista de consulta de productos con paginación, ordenación y filtrado.
     *
     * @param criterios          Criterios de búsqueda para filtrar productos
     * @param page               Número de página actual (0-indexed)
     * @param size               Tamaño de la página
     * @param sortField          Campo por el que ordenar los resultados
     * @param sortDir            Dirección de ordenación (asc/desc)
     * @param model              Modelo para pasar datos a la vista
     * @param request            Petición HTTP
     * @param session            Sesión HTTP del usuario
     * @param redirectAttributes Atributos para redirección en caso de error
     * @return Vista de consulta de productos o redirección a inicio de sesión si no hay autenticación
     */
    @GetMapping("/consulta-productos")
    public String mostrarVistaProductos(
            @ModelAttribute("criteriosBusqueda") ProductoCriteriosBusquedaDTO criterios,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "descripcion") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model,
            HttpServletRequest request,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        logger.info("[AdminController] Petición inicial: sortField='{}', sortDir='{}', page={}, size={}", sortField, sortDir, page, size);

        List<String> camposDeEntidadValidos = Arrays.asList(
                "id", "descripcion", "precio", "marca",
                "categoria.nombre", "proveedor.nombre",
                "unidades", "valoracion", "fechaAlta", "esPerecedero"
        );

        if (!camposDeEntidadValidos.contains(sortField)) {
            logger.warn("[AdminController] CAMPO DE ORDENACIÓN NO VÁLIDO: '{}'. Revirtiendo a 'descripcion'.", sortField);
            model.addAttribute("errorAlOrdenar", "El campo de ordenación '" + sortField + "' no es válido. Se ha ordenado por descripción.");
            sortField = "descripcion";
            sortDir = "asc";
        }

        logger.info("[AdminController] Usando para Pageable: sortField='{}', sortDir='{}'", sortField, sortDir);


        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortField));
        logger.info("[AdminController] Pageable Sort Config: {}", pageable.getSort());


        if (session.getAttribute("emailAutenticadoAdmin") == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para acceder a esta página.");
            return "redirect:/administrador/inicio-sesion";
        }

        String currentSortField = sortField;
        Sort.Direction currentSortDirection = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;

        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        String apiUrl = baseUrl + "/api/administrador/productos";
        String apiUrlCategorias = baseUrl + "/api/administrador/productos/categorias";
        String apiUrlProveedores = baseUrl + "/api/administrador/productos/proveedores";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("sort", currentSortField + "," + (currentSortDirection == Sort.Direction.DESC ? "desc" : "asc"));
        String apiUrlCompleta = builder.toUriString();
        logger.info("[AdminController] URL API completa construida para productos: {}", apiUrlCompleta);
        if (criterios.getDescripcion() != null && !criterios.getDescripcion().isEmpty()) {
            builder.queryParam("descripcion", criterios.getDescripcion());
        }
        // if (criterios.getCategoriaId() != null) {
        //     builder.queryParam("categoriaId", criterios.getCategoriaId().toString());
        // }
        if (criterios.getPrecioMin() != null) {
            builder.queryParam("precioMin", criterios.getPrecioMin());
        }
        if (criterios.getPrecioMax() != null) {
            builder.queryParam("precioMax", criterios.getPrecioMax());
        }
        if (criterios.getProveedorIds() != null && !criterios.getProveedorIds().isEmpty()) {
            criterios.getProveedorIds().forEach(id -> builder.queryParam("proveedorIds", id.toString()));
        }
        if (criterios.getEsPerecedero() != null) {
            builder.queryParam("esPerecedero", criterios.getEsPerecedero());
        }

        Page<ProductoResultadoDTO> paginaProductos = null;
        List<CategoriaSimpleDTO> categorias = Collections.emptyList();
        List<ProveedorSimpleDTO> proveedores = Collections.emptyList();
        try {
            // Llamada para obtener productos
            ResponseEntity<RestPage<ProductoResultadoDTO>> responseEntity = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<RestPage<ProductoResultadoDTO>>() {
                    }
            );
            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                paginaProductos = responseEntity.getBody();
            } else {
                paginaProductos = new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0);
                model.addAttribute("errorApi", "No se pudieron cargar los productos desde la API (código: " + responseEntity.getStatusCode() + ")");
            }
            // Llamada para obtener categorías
            ResponseEntity<List<CategoriaSimpleDTO>> responseEntityCategorias = restTemplate.exchange(
                    apiUrlCategorias,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<CategoriaSimpleDTO>>() {
                    }
            );
            if (responseEntityCategorias.getStatusCode().is2xxSuccessful()) {
                categorias = responseEntityCategorias.getBody();
            } else {
                model.addAttribute("errorApiFiltros", "No se pudieron cargar las categorías para los filtros.");
            }

            // Llamada para obtener proveedores
            ResponseEntity<List<ProveedorSimpleDTO>> responseEntityProveedores = restTemplate.exchange(
                    apiUrlProveedores,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ProveedorSimpleDTO>>() {
                    }
            );
            if (responseEntityProveedores.getStatusCode().is2xxSuccessful()) {
                proveedores = responseEntityProveedores.getBody();
            } else {
                model.addAttribute("errorApiFiltros", (model.containsAttribute("errorApiFiltros") ? model.getAttribute("errorApiFiltros") + " " : "") + "No se pudieron cargar los proveedores para los filtros.");
            }
        } catch (Exception e) {
            paginaProductos = new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0);
            model.addAttribute("errorApi", "Error inesperado al obtener productos: " + e.getMessage());
            System.err.println("Error llamando a la API de productos: " + e.getMessage());
            e.printStackTrace();
        }

        model.addAttribute("paginaProductos", paginaProductos);
        model.addAttribute("categorias", categorias);
        model.addAttribute("proveedores", proveedores);
        model.addAttribute("sortField", currentSortField);
        model.addAttribute("sortDir", currentSortDirection == Sort.Direction.DESC ? "desc" : "asc");
        model.addAttribute("reverseSortDir", currentSortDirection == Sort.Direction.ASC ? "desc" : "asc");

        return "consultaProductos";
    }

    /* ------------------------ DETALLE DE PRODUCTOS ----------------------------- */

    /**
     * Muestra la página de detalle de un producto.
     * @param id El UUID del producto a mostrar.
     * @param model Modelo para pasar datos a la vista.
     * @param session Sesión HTTP para control de acceso.
     * @param redirectAttributes Atributos para redirección en caso de error o no autenticación.
     * @return Nombre de la plantilla Thymeleaf para el detalle del producto o redirección.
     */
    @GetMapping("/productos/detalle/{id}") // Nuevo endpoint
    public String mostrarDetalleProducto(@PathVariable UUID id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("emailAutenticadoAdmin") == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para acceder a esta página.");
            return "redirect:/administrador/inicio-sesion";
        }

        Optional<Producto> productoOpt = productoService.obtenerProductoPorId(id);

        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            model.addAttribute("producto", producto);

            // Para facilitar el acceso en Thymeleaf al tipo específico
            if (producto instanceof Libro) {
                model.addAttribute("tipoProducto", "Libro");
                model.addAttribute("libro", (Libro) producto);
            } else if (producto instanceof Mueble) {
                model.addAttribute("tipoProducto", "Mueble");
                model.addAttribute("mueble", (Mueble) producto);
            } else if (producto instanceof Ropa) {
                model.addAttribute("tipoProducto", "Ropa");
                model.addAttribute("ropa", (Ropa) producto);
            } else {
                model.addAttribute("tipoProducto", "Desconocido");
            }
            return "detalleProducto";
        } else {
            redirectAttributes.addFlashAttribute("error", "Producto no encontrado con ID: " + id);
            return "redirect:/administrador/consulta-productos";
        }
    }

    /* ------------------------ MODIFICACION DE PRODUCTOS ----------------------------- */


    /**
     * Muestra el formulario para modificar un producto existente.
     * Carga los datos del producto identificado por el ID proporcionado y los prepara
     * para ser editados en un formulario.
     *
     * @param id    UUID del producto a modificar
     * @param model Modelo para pasar datos a la vista
     * @return Nombre de la plantilla para modificar productos o redirección en caso de error
     */
    @GetMapping("/productos/modificar/{id}")
    public String mostrarFormularioModificarProducto(@PathVariable UUID id, Model model) {
        Optional<Producto> productoOpt = productoService.obtenerProductoPorId(id);
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            ProductoModificacionDTO dto = new ProductoModificacionDTO();
            dto.setDescripcion(producto.getDescripcion());
            dto.setPrecio(producto.getPrecio());
            dto.setMarca(producto.getMarca());
            dto.setUnidades(producto.getUnidades());
            if (producto.getCategorias() != null && !producto.getCategorias().isEmpty()) {
                List<UUID> idsCategoriasActuales = producto.getCategorias().stream()
                        .map(Categoria::getId)
                        .collect(Collectors.toList());
                dto.setCategoriasIds(idsCategoriasActuales);
            } else {
                dto.setCategoriasIds(new ArrayList<>());
            }
            dto.setFechaFabricacion(producto.getFechaFabricacion());
            dto.setEsPerecedero(producto.getEsPerecedero());

            model.addAttribute("productoDTO", dto);
            model.addAttribute("productoId", id);
            model.addAttribute("todasLasCategorias", productoService.listarTodasLasCategorias());
            return "modificarProducto";
        } else {
            return "redirect:/administrador/consulta-productos?error=ProductoNoEncontrado";
        }
    }


}
