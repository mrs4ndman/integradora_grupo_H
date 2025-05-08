package org.grupo_h.comun.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor; // Lombok @AllArgsConstructor no es ideal para entidades con herencia e IDs generados.
// Constructores específicos pueden ser necesarios en subclases.

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor // Constructor sin argumentos es requerido por JPA
@Inheritance(strategy = InheritanceType.JOINED) // Estrategia de Herencia
public abstract class Producto { // Clase abstracta

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column
    private String marca;

    @Column
    private Double valoracion; // Por defecto 0 según PDF

    @Column(nullable = false)
    private Integer unidades;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", foreignKey = @ForeignKey(name = "FK_PRODUCTO_CATEGORIA"))
    private Categoria categoria; // Categoría Principal

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id", foreignKey = @ForeignKey(name = "FK_PRODUCTO_PROVEEDOR"))
    private Proveedor proveedor;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaAlta;

    @Column
    private LocalDate fechaFabricacion;

    @Column
    private Boolean esPerecedero;

    // Constructor para campos comunes
    protected Producto(String descripcion, BigDecimal precio, String marca, Integer unidades,
                       Categoria categoria, Proveedor proveedor, LocalDate fechaFabricacion, Boolean esPerecedero) {
        this.descripcion = descripcion;
        this.precio = precio;
        this.marca = marca;
        this.unidades = unidades;
        this.categoria = categoria;
        this.proveedor = proveedor;
        this.fechaFabricacion = fechaFabricacion;
        this.esPerecedero = esPerecedero;
    }


    @PrePersist
    protected void onCreate() {
        if (this.fechaAlta == null) {
            this.fechaAlta = LocalDateTime.now();
        }
        if (this.valoracion == null) {
            this.valoracion = 0.0;
        }
    }
}