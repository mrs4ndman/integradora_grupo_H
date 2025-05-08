package org.grupo_h.comun.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List; // Si una prenda puede tener múltiples tallas o colores como variantes

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "producto_ropa")
// @DiscriminatorValue("ROPA")
public class Ropa extends Producto {

    private String talla;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "producto_ropa_colores", joinColumns = @JoinColumn(name = "ropa_id"))
    @Column(name = "color")
    private List<String> coloresDisponibles;


    public Ropa(String descripcion, BigDecimal precio, String marca, Integer unidades,
                Categoria categoria, Proveedor proveedor, LocalDate fechaFabricacion, Boolean esPerecedero,
                String talla, List<String> coloresDisponibles) {
        super(descripcion, precio, marca, unidades, categoria, proveedor, fechaFabricacion, esPerecedero);
        this.talla = talla;
        this.coloresDisponibles = coloresDisponibles;
    }
}