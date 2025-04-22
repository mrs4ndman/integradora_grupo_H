package org.grupo_h.comun.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.grupo_h.comun.entity.auxiliar.CuentaCorriente;
import org.grupo_h.comun.entity.auxiliar.Direccion;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Representa un empleado en el sistema.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Empleado {

    /** Identificador único del empleado. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Nombre del empleado. No puede ser nulo. */
    @Column(nullable = false)
    private String nombre;

    /** Apellido del empleado. No puede ser nulo. */
    @Column(nullable = false)
    private String apellido;

    /** Correo electrónico del empleado. No puede ser nulo. */
    @Column(nullable = false)
    private String email;

    /** Fecha de nacimiento del empleado. No puede ser nula. */
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    // Usuario asociado al empleado (comentado).
    // @OneToOne(mappedBy = "empleado")
    // private Usuario usuario;

    /** Dirección del empleado. */
    @Embedded
    private Direccion direccion;

    /** Cuenta corriente del empleado. */
    @Embedded
    private CuentaCorriente cuentaCorriente;

    /** Fecha de alta del empleado en la base de datos. */
    @Column(name = "fecha_alta_en_BD")
    private LocalDate fechaAltaEnBaseDeDatos = LocalDate.now();

    /** Contenido del archivo asociado al empleado (Large Object). */
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "archivo_contenido", columnDefinition="LONGBLOB")
    private byte[] archivoContenido;

    /** Nombre original del archivo asociado al empleado. */
    @Column(name = "archivo_nombre_original")
    private String archivoNombreOriginal;
}
