package org.grupo_h.empleados.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO que representa los datos de registro de un empleado.
 */
@Data
public class EmpleadoRegistroDTO {

    /**
     * Identificador único del empleado.
     */
    private UUID id;

    /**
     * Nombre del empleado. No puede estar vacío.
     */
    @NotBlank
    private String nombre;

    /**
     * Apellidos del empleado. No puede estar vacío.
     */
    @NotBlank
    private String apellidos;

    /**
     * Fecha de nacimiento del empleado. Debe ser una fecha pasada.
     */
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Past(message = "{Validacion.fechaNac.Past}")
    private LocalDate fechaNacimiento;

    /**
     * Género del empleado. No requiere validación. Insertado en tabla por defecto
     */
    private String genero;

    /**
     * Correo electrónico del empleado. Debe seguir un formato válido.
     */
    @NotBlank
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "{Vaidacion.email.pattern}"
    )
    private String email;



    /**
     * Dirección del empleado.
     */
    @Valid
    private DireccionDTO direccion;

    /**
     * Cuenta corriente del empleado.
     */
    @Valid
    private CuentaCorrienteDTO cuentaCorriente;

    /**
     * Contenido del archivo adjunto.
     */
    private byte[] fotografia;

    /**
     * Nombre original del archivo adjunto.
     */
    private String archivoNombreOriginal;
}
