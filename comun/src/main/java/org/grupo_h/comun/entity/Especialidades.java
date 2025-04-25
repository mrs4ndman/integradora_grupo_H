package org.grupo_h.comun.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Especialidades {

    @Id
    private UUID id;

    @Column(name = "Especialidad")
    private String nombreEspecialidad;
}
