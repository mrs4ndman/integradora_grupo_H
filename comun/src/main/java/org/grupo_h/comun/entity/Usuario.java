package org.grupo_h.comun.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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

    /**
     * Relación inversa: un Usuario puede no tener Empleado asociado,
     * por eso optional=true (valor por defecto).
     */
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, optional = true)
    private Empleado empleado;

    /** Correo electrónico del usuario. No puede ser nulo. */
    @Column(nullable = false, unique = true)
    private String email;

    /** Contraseña del usuario. No puede ser nula. */
    @Column(nullable = false)
    private String contrasena;

    /** Indica si la cuenta está bloqueada (por ejemplo, tras intentos fallidos de ingreso). */
    @Column(nullable = false)
    private boolean cuentaBloqueada = false;

    /** Indica si la cuenta está habilitada. */
    @Column(nullable = false)
    private boolean habilitado = true;

    /** Número de intentos fallidos de inicio de sesión. No puede ser nulo. */
    @Column(nullable = false)
    private int intentosFallidos = 0;

    /** Número total de sesiones iniciadas. No puede ser nulo. */
    @Column(nullable = false)
    private int sesionesTotales = 0;

    @Column
    private LocalDateTime tiempoHastaDesbloqueo;

    @Column(length = 500)
    private String motivoBloqueo;

    @Column(length = 100)
    private String rememberMeToken;

    @Column
    private LocalDateTime rememberMeTokenExpiry;
}
