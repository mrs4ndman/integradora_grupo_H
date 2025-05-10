package org.grupo_h.empleados.service;

import org.grupo_h.empleados.dto.CategoriaSimpleDTO;
import org.grupo_h.empleados.dto.ProductoCriteriosBusquedaDTO;
import org.grupo_h.empleados.dto.ProductoResultadoDTO;
import org.grupo_h.empleados.dto.ProveedorSimpleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductoService {

    Page<ProductoResultadoDTO> buscarProductos(ProductoCriteriosBusquedaDTO criterios, Pageable pageable);

    List<CategoriaSimpleDTO> listarTodasLasCategorias();

    List<ProveedorSimpleDTO> listarTodosLosProveedores();

}