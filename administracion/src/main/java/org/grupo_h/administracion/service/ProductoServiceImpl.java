package org.grupo_h.administracion.service;

import org.grupo_h.administracion.dto.CategoriaSimpleDTO;
import org.grupo_h.administracion.dto.ProductoCriteriosBusquedaDTO;
import org.grupo_h.administracion.dto.ProductoResultadoDTO;
import org.grupo_h.administracion.dto.ProveedorSimpleDTO;
import org.grupo_h.administracion.specs.ProductoSpecification;
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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        System.out.println("Servicio usando proveedorId para spec: " + criterios.getProveedorId());
        logger.info("[ProductoService] Pageable recibido para búsqueda: {}", pageable.getSort());

        // Construir la especificación basada en los criterios
        Specification<Producto> spec = ProductoSpecification.conCriterios(
                criterios.getDescripcion(),
                criterios.getCategoriaId(),
                criterios.getPrecioMin(),
                criterios.getPrecioMax(),
                criterios.getProveedorId(),
                criterios.getEsPerecedero()
        );

        Page<Producto> productosEncontrados = productoRepository.findAll(spec, pageable);

        return productosEncontrados.map(this::convertirAProductoResultadoDTO);
    }

    private ProductoResultadoDTO convertirAProductoResultadoDTO(Producto producto) {
        ProductoResultadoDTO dto = modelMapper.map(producto, ProductoResultadoDTO.class);

        if (producto.getCategoria() != null) {
            dto.setCategoriaNombre(producto.getCategoria().getNombre());
        } else {
            dto.setCategoriaNombre("N/A");
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
            // Podrías lanzar una excepción personalizada aquí si lo prefieres
            // throw new ProductoNoEncontradoException("Producto con ID " + id + " no encontrado.");
            return; // O simplemente no hacer nada si no existe
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

        List<Producto> productosEnCategoria = productoRepository.findByCategoriaId(categoriaId);
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
        productoRepository.deleteAll(); // deleteAllInBatch() podría ser más eficiente para grandes cantidades
        long cantidadDespues = productoRepository.count();
        logger.info("Todos los productos eliminados. Antes: {}, Después: {}. Total eliminados: {}", cantidadAntes, cantidadDespues, cantidadAntes - cantidadDespues);
    }
}