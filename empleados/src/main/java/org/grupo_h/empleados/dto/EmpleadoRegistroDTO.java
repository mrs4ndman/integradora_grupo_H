package org.grupo_h.empleados.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.grupo_h.comun.entity.auxiliar.Genero;
import org.grupo_h.empleados.Validaciones.GruposValidaciones.DatosDepartamento;
import org.grupo_h.empleados.Validaciones.GruposValidaciones.DatosPersonales;
import org.grupo_h.empleados.Validaciones.GruposValidaciones.DatosRegistroDireccion;
import org.grupo_h.empleados.Validaciones.ValidacionesPersonalizadas.MinimoDosCheckbox;
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
    @NotBlank(groups = DatosPersonales.class)
    private String nombre;

    /**
     * Apellidos del empleado. No puede estar vacío.
     */
    @NotBlank(groups = DatosPersonales.class)
    private String apellidos;

    /**
     * Contenido del archivo adjunto.
     */
    @NotNull(groups = DatosPersonales.class)
    private byte[] fotografia;

    /**
     * Género del empleado. No requiere validación. Insertado en tabla por defecto
     */
//    @Pattern(
//            regexp = "^([MFO])$",
//            message = "{Validacion.generoSeleccionado.notBlank}"
//    )
    private Genero generoSeleccionado;


    /**
     * Fecha de nacimiento del empleado. Debe ser una fecha pasada.
     */
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Past(message = "{Validacion.fechaNac.Past}", groups = DatosPersonales.class)
    private LocalDate fechaNacimiento;

    /**
     * Edad del empleado.
     */
    private Integer edad;


    /**
     * País de nacimiento del empleado.
     */
    @NotNull(groups = DatosPersonales.class)
    private String paisNacimiento = "ES";

    /**
     * Comentario del empleado.
     */
    @NotNull(groups = DatosPersonales.class)
    private String comentarios;

    /**
     * Dirección del empleado.
     */
    @Valid
    private DireccionDTO direccion;

    /**
     * Tipo de Documento del empleado.
     */
    @NotNull(groups = DatosRegistroDireccion.class)
    private String tipoDocumento = "DNI";

    /**
     * Número del Documento del empleado.
     */
    @NotNull(groups = DatosRegistroDireccion.class)
    private String numeroDocumento;

    /**
     * Prefijo Internacional del teléfono móvil del empleado.
     */
    @NotNull(groups = DatosRegistroDireccion.class)
    private String prefijoTelefono ="+34";

    /**
     * Número del teléfono móvil del empleado.
     */
    @Size(min = 9, message = "{Vaidacion.telefono.Size}", groups = DatosRegistroDireccion.class)
    @Pattern(regexp = "^[0-9]+$",
            message = "{Validacion.telefono.digitos}",
            groups = DatosRegistroDireccion.class)
    @NotBlank
    private String telefonoMovil;



    /**
     * Departamento del empleado.
     */
    @NotNull(groups = DatosDepartamento.class)
    private String DepartamentoDTO;

    /**
     * Especialidades del empleado.
     */
    @MinimoDosCheckbox(groups = DatosDepartamento.class)
    @NotNull(groups = DatosDepartamento.class)
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
