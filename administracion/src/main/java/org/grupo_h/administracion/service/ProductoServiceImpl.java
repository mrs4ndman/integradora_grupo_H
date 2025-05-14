package org.grupo_h.administracion.service;

import org.grupo_h.administracion.dto.*;
import org.grupo_h.administracion.specs.ProductoSpecification;
import org.grupo_h.comun.entity.Categoria;
import org.grupo_h.comun.entity.Producto;
import org.grupo_h.comun.repository.CategoriaRepository;
import org.grupo_h.comun.repository.ProductoRepository;
// Si implementas listarCategorias/Proveedores, importa sus repositorios y DTOs
// import org.grupo_h.comun.repository.CategoriaRepository;
// import org.grupo_h.comun.repository.ProveedorRepository;
// import org.grupo_h.administracion.dto.CategoriaDTO;
// import org.grupo_h.administracion.dto.ProveedorDTO;
import org.grupo_h.comun.repository.ProveedorRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

// import java.util.List;
// import java.util.stream.Collectors;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final ModelMapper modelMapper;
     private final CategoriaRepository categoriaRepository;
     private final ProveedorRepository proveedorRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductoServiceImpl.class); // Para SLF4J


    @Autowired
    public ProductoServiceImpl(ProductoRepository productoRepository,
                               ModelMapper modelMapper, CategoriaRepository categoriaRepository, ProveedorRepository proveedorRepository
                             ) {
        this.productoRepository = productoRepository;
        this.modelMapper = modelMapper;
        this.categoriaRepository = categoriaRepository;
        this.proveedorRepository = proveedorRepository;
    }

    /**
     * Busca productos de forma paginada y filtrada según los criterios proporcionados.
     *
     * @param criterios DTO con los criterios de búsqueda.
     * @param pageable  Información de paginación y ordenación.
     * @return Una página de {@link ProductoResultadoDTO} que coincide con los criterios.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResultadoDTO> buscarProductos(ProductoCriteriosBusquedaDTO criterios, Pageable pageable) {
        logger.info("Buscando productos con criterios: {} y pageable: {}", criterios, pageable);
        logger.info("Pageable original Sort: {}", pageable.getSort());

        Sort sortForDb;
        boolean sortByPrimeraCategoria = false;
        Sort.Direction primeraCategoriaDirection = Sort.Direction.ASC;

        if (pageable.getSort().isSorted()) {
            Optional<Sort.Order> categoriaOrderOpt = StreamSupport.stream(pageable.getSort().spliterator(), false)
                    .filter(order -> "nombrePrimeraCategoria".equals(order.getProperty()))
                    .findFirst();

            if (categoriaOrderOpt.isPresent()) {
                sortByPrimeraCategoria = true;
                primeraCategoriaDirection = categoriaOrderOpt.get().getDirection();
                logger.info("Ordenación especial detectada para 'nombrePrimeraCategoria', dirección: {}. Se aplicará en memoria.", primeraCategoriaDirection);

                List<Sort.Order> otherOrders = StreamSupport.stream(pageable.getSort().spliterator(), false)
                        .filter(order -> !"nombrePrimeraCategoria".equals(order.getProperty()))
                        .collect(Collectors.toList());
                sortForDb = otherOrders.isEmpty() ? Sort.unsorted() : Sort.by(otherOrders);
            } else {
                // No hay orden por 'nombrePrimeraCategoria', usar el sort original para la BD
                sortForDb = pageable.getSort();
            }
        } else {
            // No hay ninguna ordenación solicitada
            sortForDb = Sort.unsorted();
        }

        logger.info("Sort a aplicar a la BD (sortForDb): {}", sortForDb);
        Pageable dbPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortForDb);
        logger.debug("Pageable para la consulta a BD (dbPageable): {}", dbPageable);

        // Asegúrate que los campos en ProductoCriteriosBusquedaDTO y ProductoSpecification.conCriterios coincidan
        // Tu spec usa 'descripcion' para el filtro de texto.
        Specification<Producto> spec = ProductoSpecification.conCriterios(
                criterios.getDescripcion(),
                criterios.getCategoriaId(),
                criterios.getPrecioMin(),
                criterios.getPrecioMax(),
                criterios.getProveedorIds(),
                criterios.getEsPerecedero()
                // criterios.getActivo() // Si lo habilitas de nuevo en la spec y DTO
        );

        Page<Producto> productosEncontrados;
        try {
            productosEncontrados = productoRepository.findAll(spec, dbPageable);
        } catch (Exception e) {
            // Este log es crucial si la ordenación por campos estándar falla a nivel de BD
            logger.error("Error al ejecutar findAll con la especificación y el pageable: {}. Causa: {}. Verifique las propiedades de ordenación ('{}') y los joins en la especificación.",
                    e.getMessage(),
                    e.getCause() != null ? e.getCause().getMessage() : "N/A",
                    sortForDb,
                    e);
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        List<Producto> content = new ArrayList<>(productosEncontrados.getContent());

        if (sortByPrimeraCategoria) {
            logger.debug("Aplicando ordenación en memoria por 'nombrePrimeraCategoria', dirección: {}", primeraCategoriaDirection);
            final Sort.Direction direction = primeraCategoriaDirection;
            content.sort(Comparator.comparing(producto ->
                            producto.getCategorias().stream()
                                    .map(Categoria::getNombre)
                                    .filter(nombre -> nombre != null && !nombre.isEmpty())
                                    .min(String::compareToIgnoreCase)
                                    .orElse("\uFFFF"), // Usar un carácter Unicode alto para que los vacíos vayan al final en ASC
                    (s1, s2) -> direction == Sort.Direction.ASC ? s1.compareToIgnoreCase(s2) : s2.compareToIgnoreCase(s1)
            ));
        }

        List<ProductoResultadoDTO> dtos = content.stream()
                .map(this::convertirAProductoResultadoDTO)
                .collect(Collectors.toList());

        logger.info("Encontrados {} productos en total, devolviendo página {} de {}", productosEncontrados.getTotalElements(), pageable.getPageNumber(), productosEncontrados.getTotalPages());
        return new PageImpl<>(dtos, pageable, productosEncontrados.getTotalElements());
    }

    private ProductoResultadoDTO convertirAProductoResultadoDTO(Producto producto) {
        ProductoResultadoDTO dto = modelMapper.map(producto, ProductoResultadoDTO.class);

        dto.setUnidadesDisponibles(producto.getUnidades());

        if (producto.getCategorias() != null && !producto.getCategorias().isEmpty()) {
            List<CategoriaSimpleDTO> categoriasDTO = producto.getCategorias().stream()
                    .map(cat -> {
                        CategoriaSimpleDTO catDto = new CategoriaSimpleDTO();
                        catDto.setId(cat.getId());
                        catDto.setNombre(cat.getNombre());
                        return catDto;
                    })
                    .collect(Collectors.toList());
            dto.setCategorias(categoriasDTO);
        } else {
            dto.setCategorias(new ArrayList<>());
        }
        if (producto.getProveedor() != null) {
            dto.setProveedorNombre(producto.getProveedor().getNombre());
        } else {
            dto.setProveedorNombre("N/A");
        }
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaSimpleDTO> listarTodasLasCategorias() {
        return categoriaRepository.findAll().stream()
                .map(categoria -> modelMapper.map(categoria, CategoriaSimpleDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorSimpleDTO> listarTodosLosProveedores() {
        return proveedorRepository.findAll().stream()
                .map(proveedor -> modelMapper.map(proveedor, ProveedorSimpleDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void eliminarProductoPorId(UUID id) {
        logger.info("Intentando eliminar producto con ID: {}", id);
        if (!productoRepository.existsById(id)) {
            logger.warn("Intento de eliminar un producto no existente con ID: {}", id);
            return;
        }
        productoRepository.deleteById(id);
        logger.info("Producto con ID: {} eliminado correctamente.", id);
    }

    @Override
    @Transactional
    public int eliminarProductosPorCategoria(UUID categoriaId) {
        logger.info("Intentando eliminar todos los productos de la categoría ID: {}", categoriaId);
        if (!categoriaRepository.existsById(categoriaId)) {
            logger.warn("Intento de eliminar productos de una categoría no existente con ID: {}", categoriaId);
            return 0;
        }

        List<Producto> productosEnCategoria = productoRepository.findByCategoriasId(categoriaId);
        if (productosEnCategoria.isEmpty()) {
            logger.info("No se encontraron productos en la categoría ID: {}", categoriaId);
            return 0;
        }

        logger.info("Encontrados {} productos en la categoría {}. Procesando dependencias...", productosEnCategoria.size(), categoriaId);

        int contadorEliminados = 0;
        for (Producto producto : productosEnCategoria) {
            try {
                productoRepository.delete(producto);
                contadorEliminados++;
            } catch (Exception e) {
                logger.error("Error al eliminar el producto con ID {}: {}", producto.getId(), e.getMessage(), e);
            }
        }

        logger.info("{} productos eliminados de la categoría ID: {}.", contadorEliminados, categoriaId);
        return contadorEliminados;
    }

    @Override
    @Transactional
    public void eliminarTodosLosProductos() {
        logger.info("Intentando eliminar TODOS los productos del catálogo.");
        long cantidadAntes = productoRepository.count();
        productoRepository.deleteAll();
        long cantidadDespues = productoRepository.count();
        logger.info("Todos los productos eliminados. Antes: {}, Después: {}. Total eliminados: {}", cantidadAntes, cantidadDespues, cantidadAntes - cantidadDespues);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Producto> obtenerProductoPorId(UUID id) {
        logger.debug("Solicitando producto con ID: {}", id);
        return productoRepository.findById(id);
    }

    @Override
    @Transactional
    public Optional<ProductoResultadoDTO> modificarProducto(UUID id, ProductoModificacionDTO dto) {
        Optional<Producto> productoOpt = productoRepository.findById(id);
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();

            if (dto.getDescripcion() != null && !dto.getDescripcion().isEmpty()) {
                producto.setDescripcion(dto.getDescripcion());
            }
            if (dto.getPrecio() != null) {
                producto.setPrecio(dto.getPrecio());
            }
            if (dto.getMarca() != null) {
                producto.setMarca(dto.getMarca().isEmpty() ? null : dto.getMarca());
            } else {
            }
            if (dto.getUnidades() != null) {
                producto.setUnidades(dto.getUnidades());
            }
            if (dto.getCategoriasIds() != null) {
                if (dto.getCategoriasIds().isEmpty()){
                    producto.setCategorias(new ArrayList<>());
                } else {
                    List<Categoria> nuevasCategorias = dto.getCategoriasIds().stream()
                            .map(idCategoria -> categoriaRepository.findById(idCategoria)
                                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + idCategoria + " al modificar producto " + id)))
                            .collect(Collectors.toList());
                    producto.setCategorias(nuevasCategorias);
                }
            }
            if (dto.getFechaFabricacion() != null) {
                producto.setFechaFabricacion(dto.getFechaFabricacion());
            }
            if (dto.getEsPerecedero() != null) {
                producto.setEsPerecedero(dto.getEsPerecedero());
            }

            Producto productoGuardado = productoRepository.save(producto);
            return Optional.of(convertirAProductoResultadoDTO(productoGuardado));
        }
        return Optional.empty();
    }
}