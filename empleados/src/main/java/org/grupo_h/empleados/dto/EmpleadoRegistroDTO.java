package org.grupo_h.empleados.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;
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
     * Contenido del archivo adjunto.
     */
    private byte[] fotografia;

    /**
     * Género del empleado. No requiere validación. Insertado en tabla por defecto
     */
    @Pattern(
            regexp = "^([MFO])$",
            message = "{Validacion.generoSeleccionado.notBlank}"
    )
    private String generoSeleccionado;


    /**
     * Fecha de nacimiento del empleado. Debe ser una fecha pasada.
     */
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Past(message = "{Validacion.fechaNac.Past}")
    private LocalDate fechaNacimiento;


    private Integer edad;

//    /**
//     * Correo electrónico del empleado. Debe seguir un formato válido.
//     */
//    @NotBlank
//    @Pattern(
//            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
//            message = "{Vaidacion.email.pattern}"
//    )
//    private String email;

    /**
     * País de nacimiento del empleado.
     */
    @NotNull
    private String paisNacimiento = "ES";

    /**
     * Comentario del empleado.
     */
    @NotNull
    private String comentarios;

    /**
     * Tipo de Documento del empleado.
     */
    @NotNull
    private String tipoDocumento = "DNI";

    /**
     * Número del Documento del empleado.
     */
    @NotNull
    private String numeroDocumento;

    /**
     * Prefijo Internacional del teléfono móvil del empleado.
     */
    @NotNull
    private String prefijoTelefono ="ES";

    /**
     * Número del teléfono móvil del empleado.
     */
    @Size(min = 9, message = "{Vaidacion.telefono.Size}")
    @Pattern(regexp = "^[0-9]+$",
            message = "{Validacion.telefono.digitos}")
    @NotBlank
    private String telefonoMovil;

    /**
     * Dirección del empleado.
     */
    @Valid
    private DireccionDTO direccion;

    /**
     * Departamento del empleado.
     */
    @NotNull
    private String DepartamentoDTO;

    /**
     * Especialidades del empleado.
     */
    @Size(min = 2)
    private List<String> especialidadesSeleccionadas;

    /**
     * Cuenta corriente del empleado.
     */
    @Valid
    private CuentaCorrienteDTO cuentaCorriente;

    /**
     * Nombre original del archivo adjunto.
     */
    private String archivoNombreOriginal;

    private Boolean aceptaInformacion;

}
