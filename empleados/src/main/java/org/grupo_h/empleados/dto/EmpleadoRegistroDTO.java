package org.grupo_h.empleados.dto;

import jakarta.persistence.Transient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.grupo_h.empleados.Validaciones.GruposValidaciones.*;
import org.grupo_h.empleados.Validaciones.ValidacionesPersonalizadas.DniValido;
import org.grupo_h.empleados.Validaciones.ValidacionesPersonalizadas.EdadCoherente; // Importa la anotación personalizada
import org.grupo_h.empleados.Validaciones.ValidacionesPersonalizadas.ImagenValida;
import org.grupo_h.empleados.Validaciones.ValidacionesPersonalizadas.MinimoDosCheckbox;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DTO que representa los datos de registro de un empleado.
 */
@Data
@EdadCoherente(groups = DatosPersonales.class) // Añade la validación de clase
public class EmpleadoRegistroDTO {

    /**
     * Identificador único del empleado.
     */
    private UUID UsuarioId;

    /**
     * Nombre del empleado. No puede estar vacío.
     */
    @NotBlank(groups = DatosPersonales.class)
    private String nombreDTO;

    /**
     * Apellidos del empleado. No puede estar vacío.
     */
    @NotBlank(groups = DatosPersonales.class)
    private String apellidosDTO;

    /**
     * Contenido del archivo adjunto.
     */
    @NotNull(groups = DatosPersonales.class)
    @ImagenValida(groups = DatosPersonales.class)
    private MultipartFile fotografiaDTO;

    private byte[] fotografiaArchivo;

    @Transient
    private String rutaArchivo;

    /**
     * Género del empleado. No requiere validación. Insertado en tabla por defecto
     */
    @NotNull(groups = DatosPersonales.class)
    private GeneroDTO generoSeleccionadoDTO;


    /**
     * Fecha de nacimiento del empleado. Debe ser una fecha pasada.
     */
    @NotNull(message = "{Validacion.fechaNac.vacio}", groups = DatosPersonales.class)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Past(message = "{Validacion.fechaNac.Past}", groups = DatosPersonales.class)
    private LocalDate fechaNacimientoDTO;


    @NotNull(groups = DatosPersonales.class)
    @Positive(message = "{Validacion.edad.Positive}", groups = DatosPersonales.class)
    private Integer edadDTO;

    /**
     * País de nacimiento del empleado.
     */
    @Valid
    private PaisDTO paisNacimiento;

    /**
     * Comentario del empleado.
     */
    @NotNull(groups = DatosPersonales.class)
    private String comentarios;

    /**
     * Tipo de Documento del empleado.
     */
    @NotNull(groups = DatosRegistroDireccion.class)
    private TipoDocumentoDTO tipoDocumentoDTO;

    /**
     * Número del Documento del empleado.
     */
    @DniValido(groups = DatosRegistroDireccion.class)
    @NotNull(groups = DatosRegistroDireccion.class)
    private String numeroDocumentoDTO;

    /**
     * Prefijo Internacional del teléfono móvil del empleado.
     */
    @NotNull(groups = DatosRegistroDireccion.class)
    private String prefijoTelefono = "+34";

    /**
     * Número del teléfono móvil del empleado.
     */
    @Size(min = 9, message = "{Vaidacion.telefono.Size}",groups = DatosRegistroDireccion.class)
    @Pattern(regexp = "^[0-9]+$",
            message = "{Validacion.telefono.digitos}",
            groups = DatosRegistroDireccion.class)
    @NotBlank(groups = DatosRegistroDireccion.class)
    private String telefonoMovil;

    /**
     * Dirección del empleado.
     */
    @Valid
    private DireccionDTO direccionDTO;

    /**
     * Departamento del empleado.
     */
    @NotNull(groups = DatosDepartamento.class)
    private DepartamentoDTO DepartamentoDTO;

    /**
     * Especialidades del empleado.
     */
    @MinimoDosCheckbox(groups = DatosDepartamento.class)
    @Valid
    private List<EspecialidadesEmpleadoDTO> especialidadesSeleccionadasDTO = new ArrayList<>();


    @NotNull(message = "{Validacion.Salario.Notnull}",
            groups = DatosFinancieros.class)
    @Digits(integer = 8,
            fraction = 2,
            message = "{Validacion.Salario.Digits}",
            groups = DatosFinancieros.class)
    @PositiveOrZero(message = "{Validacion.Salario.PositiveOrZero}",
            groups = DatosFinancieros.class)
    private Double salarioDTO;

    /**
     * Comisión del empleado.
     */
    @NotNull(message = "{Validacion.Comision.Notnull}",
            groups = DatosFinancieros.class)
    @Digits(integer = 8,
            fraction = 2,
            message = "{Validacion.Comision.Digits}",
            groups = DatosFinancieros.class)
    @PositiveOrZero(message = "{Validacion.Comision.PositiveOrZero}",
            groups = DatosFinancieros.class)
    private Double comisionDTO;

    @Valid
    private CuentaCorrienteDTO cuentaCorrienteDTO;

    @Valid
    private TarjetaCreditoDTO tarjetasCreditoDTO;

    @NotNull(groups = DatosFinales.class)
    @AssertTrue(message = "{Validacion.Consentimiento.true}",
            groups = DatosFinales.class)
    private Boolean aceptaInformacionDTO;

    //    /**
//     * Nombre original del archivo adjunto.
//     */
//
//    private String archivoNombreOriginal;

}