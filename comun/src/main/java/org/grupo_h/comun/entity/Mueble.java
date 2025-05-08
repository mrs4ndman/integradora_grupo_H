package org.grupo_h.comun.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.grupo_h.comun.entity.auxiliar.Dimensiones;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "producto_mueble")
public class Mueble extends Producto {

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="ancho", column=@Column(name="dimensiones_ancho")),
            @AttributeOverride(name="profundo", column=@Column(name="dimensiones_profundo")),
            @AttributeOverride(name="alto", column=@Column(name="dimensiones_alto"))
    })
    private Dimensiones dimensiones;

    @ElementCollection(fetch = FetchType.LAZY) // Para colecciones de tipos básicos o embeddables
    @CollectionTable(name = "producto_mueble_colores", joinColumns = @JoinColumn(name = "mueble_id"))
    @Column(name = "color")
    private List<String> colores;

    private Double peso;

    public Mueble(String descripcion, BigDecimal precio, String marca, Integer unidades,
                  Categoria categoria, Proveedor proveedor, LocalDate fechaFabricacion, Boolean esPerecedero,
                  Dimensiones dimensiones, List<String> colores, Double peso) {
        super(descripcion, precio, marca, unidades, categoria, proveedor, fechaFabricacion, esPerecedero);
        this.dimensiones = dimensiones;
        this.colores = colores;
        this.peso = peso;
    }
}