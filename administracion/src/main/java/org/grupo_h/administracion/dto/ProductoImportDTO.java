package org.grupo_h.administracion.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // Ignora campos en JSON que no estén en el DTO
public class ProductoImportDTO {

    // Campos Comunes (de la clase Producto)
    private String descripcion;
    private BigDecimal precio;
    private String marca;
    private List<String> categorias; //= new ArrayList<>();

    private Boolean esPerecedero;

    private Integer unidades;
    private String fechaFabricacion;

    // --- Campos Específicos para LIBRO ---
    private String titulo;
    private String autor;
    private String editorial;
    private String tapa;
    private Integer numeroPaginas;
    private Boolean segundaMano;

    // --- Campos Específicos para MUEBLE ---
    private DimensionesDTO dimensiones;
    @JsonAlias("colores")
    private List<String> coloresMueble;
    private Double peso;

    // --- Campos Específicos para ROPA ---
    // (Asumiendo nombres de campos para el JSON)
    private String talla;
    private List<String> coloresRopa;

    // Campo para identificar el tipo de producto si el JSON lo proveyera explícitamente.
    // Si no, lo inferiremos en el servicio.
    private String tipoProducto;
}