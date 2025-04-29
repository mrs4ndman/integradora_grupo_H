package org.grupo_h.empleados.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.grupo_h.comun.entity.auxiliar.CuentaCorriente;
import org.grupo_h.comun.entity.auxiliar.Direccion;
import org.grupo_h.comun.entity.auxiliar.Genero;

import java.time.LocalDate;

/**
 * DTO que representa los detalles de un empleado.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmpleadoDetalleDTO {
    /**
     * Nombre del empleado.
     */
    private String nombre;

    /**
     * Apellido del empleado.
     */
    private String apellidos;

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
    private String cuentaCorriente;

    /**
     * Género del empleado.
     */
    private Genero genero;  // Cambiado a la entidad Genero
}
