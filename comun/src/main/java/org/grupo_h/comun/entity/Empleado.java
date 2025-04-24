package org.grupo_h.comun.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.grupo_h.comun.entity.auxiliar.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Representa un empleado en el sistema.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SecondaryTable(name = "datosEconomicos",
        pkJoinColumns = @PrimaryKeyJoinColumn(name = "empleados_id"),
        foreignKey = @ForeignKey(name = "FK_EMPLEADO_DATOS_ECONOMICOS")
)
public class Empleado {

    /**
     * Identificador único del empleado.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Nombre del empleado. No puede ser nulo.
     */
    @Column(nullable = false)
    private String nombre;

    /**
     * Apellido del empleado. No puede ser nulo.
     */
    @Column(nullable = false)
    private String apellidos;

    /**
     * Contenido del archivo asociado al empleado (Large Object).
     */
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "fotografia", columnDefinition = "LONGBLOB")
    private byte[] fotografia;

    /**
     * Género del empleado. Es un Enum alojado en entity/auxiliar
     */
    @ManyToOne
    @JoinColumn(name = "codigo_genero", nullable = false)
    private Genero generoSeleccionado;

    /**
     * Correo electrónico del empleado. No puede ser nulo.
     */
    @Column(nullable = false)
    private String email;

    /**
     * Fecha de nacimiento del empleado. No puede ser nula.
     */
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    // Usuario asociado al empleado (comentado).
    // @OneToOne(mappedBy = "empleado")
    // private Usuario usuario;

    /**
     * Edad del empleado.
     */
    @Column(name = "Edad")
    private Integer edad;

    /**
     * País de Nacimiento del empleado.
     */
    @ManyToOne
    @JoinColumn(
            name = "pais_nacimiento",
            foreignKey = @ForeignKey(name = "FK_PAIS_NACIMIENTO_EMPLEADO")
    )
    private Pais paisNacimiento;

    /**
     * Comentario del empleado.
     */
    @Column(name = "Comentarios")
    private String comentarios;

    /**
     * Tipo de Documento de Identidad del empleado.
     */
    @ManyToOne
    @JoinColumn(name = "tipo_documento_cod_tipo_doc",
                foreignKey = @ForeignKey(name = "FK_TIPO_DOCUMENTO_EMPLEADO")
    )
    private TipoDocumento tipoDocumento;

    /**
     * Número del Documento de Identidad del empleado.
     */
    @Column(name = "Documento")
    private String documento;

    /**
     * Prefijo del teléfono móvil del empleado.
     */
    @Column(name = "Prefijo_Internacional_Telf")
    private String prefijoTelefono;

    /**
     * Teléfono móvil del empleado.
     */
    @Column(name = "Telf_Movil")
    private String telefonoMovil;

    /**
     * Dirección del empleado.
     */
    @Embedded
    private Direccion direccion;

    /**
     * Fecha de alta del empleado en la base de datos.
     */
    @Column(name = "fecha_alta_en_BD")
    private LocalDate fechaAltaEnBaseDeDatos = LocalDate.now();

    /**
     * Nombre original del archivo asociado al empleado.
     */
    @Column(name = "archivo_nombre_original")
    private String archivoNombreOriginal;


    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_corriente_id",
            foreignKey = @ForeignKey(name = "FK_EMPLEADO_CUENTA_CORRIENTE"))
    private CuentaCorriente cuentaCorriente;


    @Column(name="Salario", table = "datosEconomicos")
    private Double salario;

    @Column(name="Comision", table = "datosEconomicos")
    private Double comision;

    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TarjetaCredito> tarjetasCredito;
}
