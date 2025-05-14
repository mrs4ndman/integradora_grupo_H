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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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
        logger.info("[ProductoService] Pageable recibido para búsqueda: {}", pageable.getSort());

        // Construir la especificación basada en los criterios
        Specification<Producto> spec = ProductoSpecification.conCriterios(
                criterios.getDescripcion(),
                criterios.getCategoriaId(),
                criterios.getPrecioMin(),
                criterios.getPrecioMax(),
                criterios.getProveedorIds(),
                criterios.getEsPerecedero()
        );

        Page<Producto> productosEncontrados = productoRepository.findAll(spec, pageable);

        return productosEncontrados.map(this::convertirAProductoResultadoDTO);
    }

    private ProductoResultadoDTO convertirAProductoResultadoDTO(Producto producto) {
        ProductoResultadoDTO dto = modelMapper.map(producto, ProductoResultadoDTO.class);

        dto.setUnidadesDisponibles(producto.getUnidades());

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

    @Override
    @Transactional(readOnly = true)
    public Optional<Producto> obtenerProductoPorId(UUID id) {
        logger.debug("Solicitando producto con ID: {}", id);
        return productoRepository.findById(id);
    }

    @Override
    @Transactional
    public Optional<ProductoResultadoDTO> modificarProducto(UUID id, ProductoModificacionDTO dto) { // MODIFICADO: Tipo de retorno
        Optional<Producto> productoOpt = productoRepository.findById(id);
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();

            if (dto.getDescripcion() != null && !dto.getDescripcion().isEmpty()) {
                producto.setDescripcion(dto.getDescripcion());
            }
            if (dto.getPrecio() != null) {
                producto.setPrecio(dto.getPrecio());
            }
            // Para marca, si quieres permitir limpiarla, podrías hacer:
            if (dto.getMarca() != null) {
                producto.setMarca(dto.getMarca().isEmpty() ? null : dto.getMarca());
            } else {
                // Si dto.getMarca() es null, significa que el campo no venía en el JSON o no se quiere modificar.
                // Si quisieras poder poner marca a null explícitamente desde el formulario enviando un valor vacío:
                // if (dto.getMarca() != null && dto.getMarca().isEmpty()) producto.setMarca(null);
                // else if (dto.getMarca() != null) producto.setMarca(dto.getMarca());
                // La lógica actual es: si se envía "marca": "" -> se pone a null. Si se envía "marca": "valor" -> se actualiza. Si no se envía "marca" -> no se toca.
            }
            if (dto.getUnidades() != null) {
                producto.setUnidades(dto.getUnidades());
            }
            if (dto.getCategoriaId() != null) {
                Optional<Categoria> categoriaOpt = categoriaRepository.findById(dto.getCategoriaId());
                // Decide qué hacer si la categoría no se encuentra. ¿Lanzar excepción, ignorar, loggear?
                // Por ahora, si se encuentra, se asigna.
                categoriaOpt.ifPresent(producto::setCategoria);
                if (categoriaOpt.isEmpty()) {
                    logger.warn("Categoría con ID {} no encontrada al modificar producto {}", dto.getCategoriaId(), id);
                    // Considera si esto debe ser un error que impida la modificación o solo un warning.
                    // Dependiendo del requisito, podrías lanzar una DataIntegrityViolationException o similar.
                }
            }
            if (dto.getFechaFabricacion() != null) {
                producto.setFechaFabricacion(dto.getFechaFabricacion());
            }
            if (dto.getEsPerecedero() != null) {
                producto.setEsPerecedero(dto.getEsPerecedero());
            }

            Producto productoGuardado = productoRepository.save(producto);
            // MODIFICADO: Convertir a DTO antes de retornar
            return Optional.of(convertirAProductoResultadoDTO(productoGuardado));
        }
        return Optional.empty();
    }
}