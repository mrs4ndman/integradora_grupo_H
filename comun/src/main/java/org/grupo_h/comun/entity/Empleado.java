package org.grupo_h.comun.entity;

import jakarta.persistence.*;
import lombok.*;
import org.grupo_h.comun.entity.auxiliar.*; // Asegúrate que Genero está aquí

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Representa un empleado en el sistema.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SecondaryTable(name = "datos_Economicos",
        pkJoinColumns = @PrimaryKeyJoinColumn(name = "empleados_id"),
        foreignKey = @ForeignKey(name = "FK_EMPLEADO_DATOS_ECONOMICOS")
)
public class Empleado {

    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "id") // Ajusta 'usuario_id' al nombre real de tu columna FK
    @EqualsAndHashCode.Exclude
    private Usuario usuario;

    // DATOS PERSONALES

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellidos;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "fotografia", columnDefinition = "LONGBLOB")
    private byte[] fotografia;

    /**
     * Género del empleado. Relación ManyToOne con la entidad Genero.
     * La columna 'codigo_genero' en la tabla Empleado almacenará la FK.
     * MODIFICACIÓN: Renombrado de 'generoSeleccionado' a 'genero' para consistencia.
     */
    @ManyToOne(optional = false) // Asumiendo que el género es obligatorio
    @JoinColumn(name = "codigo_genero", nullable = false,
            foreignKey = @ForeignKey(name = "FK_EMPLEADO_GENERO")) // Opcional: Nombre explícito FK
    private Genero genero; // <-- CAMBIO DE NOMBRE AQUÍ

    @Column(name="email")
    private String email;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    // @OneToOne(mappedBy = "empleado")
    // private Usuario usuario; // Comentado, ya existe la relación arriba con @MapsId

    @Column(name = "edad")
    private Integer edad; // Considerar calcularla en lugar de almacenarla si es posible

    @ManyToOne
    @JoinColumn(
            name = "pais_nacimiento",
            foreignKey = @ForeignKey(name = "FK_PAIS_NACIMIENTO_EMPLEADO")
    )
    private Pais paisNacimiento;

    @Column(name = "comentarios")
    private String comentarios;


    // DATOS DE CONTACTO

    @ManyToOne
    @JoinColumn(name = "tipo_documento_cod_tipo_doc",
            foreignKey = @ForeignKey(name = "FK_TIPO_DOCUMENTO_EMPLEADO")
    )
    private TipoDocumento tipoDocumento;

    @Column(name = "numero_documento")
    private String numeroDocumento;

    @Column(name = "prefijo_internacional_telf")
    private String prefijoTelefono;

    @Column(name = "telf_movil")
    private String telefonoMovil;

    @Embedded
    private Direccion direccion;


    @Column(name = "archivo_nombre_original")
    private String archivoNombreOriginal;

    @ManyToOne
    @JoinColumn(name = "departamento_id")
    @EqualsAndHashCode.Exclude
    private Departamento departamento;

    @ManyToMany
    @JoinTable(
            name = "empleado_especialidades_empleado",
            joinColumns = @JoinColumn(name = "empleado_id"),
            inverseJoinColumns = @JoinColumn(name = "especialidades_empleado_id")
    )
    private List<EspecialidadesEmpleado> especialidadesEmpleado;

    // Tabla Secundaria Datos Económicos

    @Column(table = "datos_Economicos")
    private double salario;

    @Column(table = "datos_Economicos")
    private double comision;

    @ManyToOne
    private CuentaCorriente cuentaCorriente;


    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "tiposTarjetaCredito", column = @Column(table = "datos_Economicos", name = "tipo_tarjeta_credito")),
            @AttributeOverride(name = "numeroTarjetaCredito", column = @Column(table = "datos_Economicos", name = "numero_tarjeta_credito")),
            @AttributeOverride(name = "cvv", column = @Column(table = "datos_Economicos", name = "cvv")),
            @AttributeOverride(name = "mesCaducidad", column = @Column(table = "datos_Economicos", name = "mes_caducidad")),
            @AttributeOverride(name = "anioCaducidad", column = @Column(table = "datos_Economicos", name = "anio_caducidad"))
    })
    private TarjetaCredito tarjetas;

    // Fin Tabla DAtos Económicos

    private Boolean aceptaInformacion;

    @Column(name = "fecha_alta_en_BD")
    private LocalDate fechaAltaEnBaseDeDatos = LocalDate.now();

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "empleado_etiqueta",
            joinColumns = @JoinColumn(name = "empleado_id"),
            inverseJoinColumns = @JoinColumn(name = "etiqueta_id"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Etiqueta> etiquetas = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jefe_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Empleado jefe;

    @OneToMany(mappedBy = "jefe", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Empleado> subordinados = new HashSet<>();

    @Version
    private Integer version;

    public void addEtiqueta(Etiqueta etiqueta) {
        this.etiquetas.add(etiqueta);
        // Si la relación fuera bidireccional (Etiqueta tuviera Set<Empleado>),
        // también añadiríamos this a etiqueta.getEmpleados().add(this);
    }

    public void removeEtiqueta(Etiqueta etiqueta) {
        this.etiquetas.remove(etiqueta);
        // Si fuera bidireccional: etiqueta.getEmpleados().remove(this);
    }

    public void addSubordinado(Empleado subordinado) {
        this.subordinados.add(subordinado);
        subordinado.setJefe(this);
    }

    public void removeSubordinado(Empleado subordinado) {
        this.subordinados.remove(subordinado);
        subordinado.setJefe(null);
    }
}