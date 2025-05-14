package org.grupo_h.empleados.service;

import org.grupo_h.empleados.dto.CategoriaSimpleDTO;
import org.grupo_h.empleados.dto.ProductoCriteriosBusquedaDTO;
import org.grupo_h.empleados.dto.ProductoResultadoDTO;
import org.grupo_h.empleados.dto.ProveedorSimpleDTO;
import org.grupo_h.empleados.specs.ProductoSpecification;
import org.grupo_h.comun.entity.Producto;
import org.grupo_h.comun.repository.CategoriaRepository;
import org.grupo_h.comun.repository.ProductoRepository;
import org.grupo_h.comun.repository.ProveedorRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    @Transactional(readOnly = true)
    public Optional<Producto> obtenerProductoPorId(UUID id) {
        logger.debug("[EmpleadoProductoService] Solicitando producto con ID: {}", id);
        // El ProductoRepository es común, así que esta llamada es la misma
        return productoRepository.findById(id);
    }
}