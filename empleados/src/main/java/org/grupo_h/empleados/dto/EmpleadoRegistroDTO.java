package org.grupo_h.empleados.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.grupo_h.comun.entity.auxiliar.Genero;
import org.grupo_h.empleados.validation.EdadCoherente; // Importa la anotación personalizada
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
@EdadCoherente // Añade la validación de clase
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
    @Size(max = 204800, message = "{Validacion.fotografia.tamanio}")
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
     * Género del empleado. No requiere validación. Insertado en tabla por defecto
     */
    @Pattern(
            regexp = "^([MFO])$",
            message = "{Validacion.generoSeleccionado.notBlank}"
    )
    private String genero;


    /**
     * Fecha de nacimiento del empleado. Debe ser una fecha pasada.
     */
    @NotNull(message = "{Validacion.fechaNac.vacio}")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Past(message = "{Validacion.fechaNac.Past}", groups = DatosPersonales.class)
    private LocalDate fechaNacimiento;


    @NotNull
    @Positive(message = "{Validacion.edad.Positive}")
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
    private String prefijoTelefono = "ES";

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
    private DireccionDTO direccionDTO;

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

    private CuentaCorrienteDTO cuentaCorrienteDTO;

    /**
     * Nombre original del archivo adjunto.
     */
    @Pattern(regexp = ".*\\.(gif|jpg|jpeg|png)$", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "{Validacion.fotografia.formato}")
    private String archivoNombreOriginal;

    private Boolean aceptaInformacion;

}