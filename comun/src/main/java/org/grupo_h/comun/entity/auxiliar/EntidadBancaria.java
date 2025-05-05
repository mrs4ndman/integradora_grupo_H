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
@Table(name = "entidad_bancaria")
@Data
@NoArgsConstructor
public class EntidadBancaria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_entidad", length = 4, nullable = false, unique = true)
    private String codigoEntidad;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "siglas")
    private String siglas;

    @ManyToOne
    @JoinColumn(name = "pais_id")
    private Pais pais;
}
