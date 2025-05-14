package org.grupo_h.administracion.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoCriteriosBusquedaDTO {
    private String descripcion;
    private UUID categoriaId;
    private Double precioMin;
    private Double precioMax;
    private List<UUID> proveedorIds;
    private Boolean esPerecedero;
}