package org.grupo_h.empleados.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResultadoDTO {
    private UUID id;
    private String descripcion;
    private Double precio;
    private List<CategoriaSimpleDTO> categorias;
    private String proveedorNombre;
    private String marca;
    private Double valoracionMedia;
    private Integer unidadesDisponibles;
    private boolean esPerecedero;
}