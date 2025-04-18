package org.grupo_h.comun.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String nombreUsuario;

    @Column(nullable = false)
    private String contrasena;

    @Column(nullable = false)
    private String email;

    // Indicador para saber si la cuenta está bloqueada, por ejemplo, tras intentos fallidos de ingreso
    private boolean cuentaBloqueada = false;

    // Flag para determinar si la cuenta está activa
    private boolean habilitado = true;
}
