package org.grupo_h.comun.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Representa un usuario en el sistema.
 */
@Entity
@Data
@NoArgsConstructor
public class Usuario {

    /** Identificador único del usuario. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Nombre de usuario. Debe ser único y no puede ser nulo. */
    @Column(nullable = false, unique = true)
    private String nombreUsuario;

    /** Contraseña del usuario. No puede ser nula. */
    @Column(nullable = false)
    private String contrasena;

    /** Correo electrónico del usuario. No puede ser nulo. */
    @Column(nullable = false)
    private String email;

    /** Indica si la cuenta está bloqueada (por ejemplo, tras intentos fallidos de ingreso). */
    private boolean cuentaBloqueada = false;

    /** Indica si la cuenta está habilitada. */
    private boolean habilitado = true;

    /** Número de intentos fallidos de inicio de sesión. No puede ser nulo. */
    @Column(nullable = false)
    private int intentosFallidos = 0;

    /** Número total de sesiones iniciadas. No puede ser nulo. */
    @Column(nullable = false)
    private int sesionesTotales = 0;
}
