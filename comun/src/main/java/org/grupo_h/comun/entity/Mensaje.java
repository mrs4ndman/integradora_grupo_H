package org.grupo_h.comun.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Mensaje {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Past
    private LocalDateTime fechaEmision;

    @OneToOne
    private Empleado emisor;

    @OneToOne
    private Empleado receptor;

    @NotBlank
    private String mensaje;


}
