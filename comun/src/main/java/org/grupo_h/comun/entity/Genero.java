package org.grupo_h.comun.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Genero {
    @Id
    @Column(nullable = false,
            unique = true,
            length = 1)
    private String codigoGenero;
    @Column(name = "genero")
    private String nombreGenero;

}
