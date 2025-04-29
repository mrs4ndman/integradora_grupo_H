package org.grupo_h.empleados.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.grupo_h.comun.entity.auxiliar.Genero;
import org.grupo_h.comun.repository.GeneroRepository;
import org.grupo_h.empleados.Validaciones.ValidacionesPersonalizadas.DniValido;
import org.grupo_h.empleados.Validaciones.ValidacionesPersonalizadas.EdadCoherente; // Importa la anotación personalizada
import org.grupo_h.empleados.Validaciones.GruposValidaciones.DatosDepartamento;
import org.grupo_h.empleados.Validaciones.GruposValidaciones.DatosPersonales;
import org.grupo_h.empleados.Validaciones.GruposValidaciones.DatosRegistroDireccion;
import org.grupo_h.empleados.Validaciones.ValidacionesPersonalizadas.MinimoDosCheckbox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
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
    private UUID id;

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
    @Size(max = 204800,
            message = "{Validacion.fotografia.tamanio}",
            groups = DatosPersonales.class)
//    @Pattern(regexp = ".*\\.(gif|jpg|jpeg|png)$", flags = Pattern.Flag.CASE_INSENSITIVE,
//            message = "{Validacion.fotografia.formato}",
//            groups = DatosPersonales.class)
    private byte[] fotografia;

//    /**
//     * Género del empleado. No requiere validación. Insertado en tabla por defecto
//     */
////    @Pattern(
////            regexp = "^([MFO])$",
////            message = "{Validacion.generoSeleccionado.notBlank}",
////            groups = DatosPersonales.class
////    )
//    private Genero generoSeleccionado;



    /**
     * Género del empleado. No requiere validación. Insertado en tabla por defecto
     */
    @Pattern(
            regexp = "^([MFO])$",
            message = "{Validacion.generoSeleccionado.notBlank}",
            groups = DatosPersonales.class
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
    @Positive(message = "{Validacion.edad.Positive}", groups = DatosPersonales.class)
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
    @NotNull(groups = DatosRegistroDireccion.class)
    private String tipoDocumento = "DNI";

    /**
     * Número del Documento del empleado.
     */
    @DniValido(groups = DatosRegistroDireccion.class)
    @NotNull(groups = DatosRegistroDireccion.class)
    private String numeroDocumento;

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
    private String DepartamentoDTO;

    /**
     * Especialidades del empleado.
     */
    @MinimoDosCheckbox(groups = DatosDepartamento.class)
    private List<String> especialidadesSeleccionadasDTO;

    @Valid
    private DatosEconomicosDTO datosEconomicosDTO;

    //    /**
//     * Nombre original del archivo adjunto.
//     */
//
//    private String archivoNombreOriginal;
    @NotNull
    @AssertTrue(message = "{Validacion.Consentimiento.true}")
    private Boolean aceptaInformacionDTO;

}