package org.grupo_h.administracion.service;

import org.grupo_h.administracion.dto.CategoriaSimpleDTO;
import org.grupo_h.administracion.dto.ProductoCriteriosBusquedaDTO;
import org.grupo_h.administracion.dto.ProductoResultadoDTO;
// import org.grupo_h.administracion.dto.CategoriaDTO;
// import org.grupo_h.administracion.dto.ProveedorDTO;
import org.grupo_h.administracion.dto.ProveedorSimpleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ProductoService {

    Page<ProductoResultadoDTO> buscarProductos(ProductoCriteriosBusquedaDTO criterios, Pageable pageable);

    List<CategoriaSimpleDTO> listarTodasLasCategorias();

    List<ProveedorSimpleDTO> listarTodosLosProveedores();

    void eliminarProductoPorId(UUID id);

    int eliminarProductosPorCategoria(UUID categoriaId);

    void eliminarTodosLosProductos();
}