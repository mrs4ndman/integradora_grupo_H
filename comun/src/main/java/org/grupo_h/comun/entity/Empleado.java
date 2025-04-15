package org.grupo_h.comun.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Relaciones
    @OneToMany(mappedBy = "empleado")
    private List<Colaboracion> colaboraciones;

    @OneToMany(mappedBy = "remitente")
    private List<Mensaje> mensajesEnviados;

    @OneToMany(mappedBy = "destinatario")
    private List<Mensaje> mensajesRecibidos;
}
