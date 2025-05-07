package org.grupo_h.comun.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table
public class Nomina {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "anio_nomina")
    @Min(0)
    private int anioNomina;

    @Column(name = "mes_nomina")
    @Min(1)
    @Max(12)
    private int mesNomina;

    @OneToMany(cascade = CascadeType.ALL)
    private List<LineaNomina> lineas;
}
