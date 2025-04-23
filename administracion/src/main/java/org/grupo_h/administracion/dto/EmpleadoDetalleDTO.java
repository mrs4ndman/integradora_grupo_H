package org.grupo_h.administracion.dto;

import lombok.Data;
import org.grupo_h.comun.entity.auxiliar.CuentaCorriente;
import org.grupo_h.comun.entity.auxiliar.Direccion;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) que contiene información detallada de un empleado.
 * Utilizado para transferir datos completos de un empleado entre capas de la aplicación.
 */
@Data
public class EmpleadoDetalleDTO {
    /**
     * Identificador único del empleado
     */
    private UUID id;
    /**
     * Nombre del empleado
     */
    private String nombre;
    /**
     * Apellido del empleado
     */
    private String apellidos;
    /**
     * Correo electrónico del empleado
     */
    private String email;
    /**
     * Fecha de nacimiento del empleado
     */
    private LocalDate fechaNacimiento;
    /**
     * Información de la dirección del empleado
     */
    private Direccion direccion;
    /**
     * Información de la cuenta corriente del empleado
     */
    private CuentaCorriente cuentaCorriente;
    /**
     * Fecha en que el empleado fue dado de alta en el sistema
     */
    private LocalDate fechaAltaEnBaseDeDatos;
}

