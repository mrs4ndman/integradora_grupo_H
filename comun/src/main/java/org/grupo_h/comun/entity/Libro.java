package org.grupo_h.comun.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "producto_libro") // Nombre de la tabla para esta subclase
public class Libro extends Producto {

    @Column(nullable = false)
    private String titulo;

    private String autor;
    private String editorial;
    private String tapa; // Blanda, Dura, etc.
    private Integer numeroPaginas;
    private Boolean segundaMano;
}