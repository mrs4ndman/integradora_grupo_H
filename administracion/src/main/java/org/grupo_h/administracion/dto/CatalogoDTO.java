package org.grupo_h.administracion.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class CatalogoDTO {
    private String proveedor;
    private List<ProductoImportDTO> productos = new ArrayList<>();
}