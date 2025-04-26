package org.grupo_h.comun.entity.auxiliar;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Representa una entidad bancaria en el sistema (ej: bancos, cajas de ahorro).
 * Esta entidad almacena información financiera y su relación con un país específico.
 *
 * <p>Ejemplo de uso:</p>
 * <pre>
 * {@code
 * Pais paisEspana = new Pais("ES", "España");
 * EntidadBancaria banco = new EntidadBancaria("BSCH", "Banco Santander", paisEspana);
 * }
 * </pre>
 *
 * @see Pais
 * @since 1.0
 */
@Entity
@Data
@NoArgsConstructor
public class EntidadBancaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Código único que identifica la entidad bancaria (ej: "BSCH" para Banco Santander).
     * Este campo actúa como clave primaria en la base de datos.
     *
     * <p>Formato: 4 caracteres alfanuméricos (estándar bancario).</p>
     */

    @Column(name = "codigo_entidad_bancaria",
            length = 4,
            nullable = false,
            unique = true)
    private String codigoEntidadBancaria;

    /**
     * Nombre completo de la entidad bancaria (ej: "Banco Bilbao Vizcaya Argentaria").
     *
     * <p>Requisitos:
     * <ul>
     *   <li>No puede ser nulo</li>
     *   <li>Longitud máxima: 100 caracteres</li>
     * </ul>
     */
    @Column(name = "Entidad_Bancaria")
    private String nombreEntidadBancaria;

    @Column(name ="Siglas_Entidad")
    private String siglasEntidad;

    /**
     * País donde está registrada la entidad bancaria.
     * Relación Many-to-One: Muchas entidades bancarias pueden pertenecer a un mismo país.
     *
     * <p>La columna "pais_cod_pais" en la tabla almacena la clave foránea al país.</p>
     *
     * @see Pais
     */
    @ManyToOne
    @JoinColumn(name = "pais_cod_pais",
            foreignKey = @ForeignKey(name = "FK_PAIS_ENTIDAD_BANCARIA"))
    private Pais pais;

}
