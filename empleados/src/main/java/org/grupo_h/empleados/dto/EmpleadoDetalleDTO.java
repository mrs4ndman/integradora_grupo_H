package org.grupo_h.empleados.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.grupo_h.comun.entity.auxiliar.CuentaCorriente;
import org.grupo_h.comun.entity.auxiliar.Direccion;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO que representa los detalles de un empleado.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmpleadoDetalleDTO {

    /**
     * Identificador único del empleado.
     */
    private UUID id;

    /**
     * Nombre del empleado.
     */
    private String nombre;

    /**
     * Apellido del empleado.
     */
    private String apellido;

    /**
     * Correo electrónico del empleado.
     */
    private String email;

    /**
     * Fecha de nacimiento del empleado.
     */
    private LocalDate fechaNacimiento;

    /**
     * Dirección del empleado.
     */
    private Direccion direccion;

    /**
     * Cuenta corriente del empleado.
     */
    private CuentaCorriente cuentaCorriente;

    /**
     * Fecha de alta del empleado en la base de datos.
     */
    private LocalDate fechaAltaEnBaseDeDatos;
}
