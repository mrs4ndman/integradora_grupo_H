package org.grupo_h.comun.entity.auxiliar;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa una dirección postal.
 */
@Data
@NoArgsConstructor
@Embeddable
public class Direccion {

    /** Tipo de vía (calle, avenida, etc.). */

    @ManyToOne
    @JoinColumn(name = "tipo_via_dirreccion_ppal_id",
                referencedColumnName = "id",
                foreignKey = @ForeignKey(name = "FK_TIPOVIA_DIRECCION"))
    private TipoVia tipoViaDirreccionPpal;

    /** Nombre de la vía. */
    @Column(name = "Nombre_via")
    private String nombreViaDirreccionPpal;

    /** Número de la dirección. */
    @Column(name = "numero")
    private int numeroViaDirreccionPpal;

    /** Portal de la dirección. */
    @Column(name = "Portal")
    private String portalDirreccionPpal;

    /** Planta de la dirección. */
    @Column(name = "Planta")
    private String plantaDirreccionPpal;

    /** Puerta de la dirección. */
    @Column(name = "Puerta")
    private String puertaDirreccionPpal;

    /** Localidad o ciudad. */
    @Column(name = "localidad")
    private String localidadDirreccionPpal;

    /** Región/Comunidad Autónoma o Estado. */
    @Column(name = "region")
    private String regionDirreccionPpal;

    /** Código postal. */
    @Column(name = "Cod_postal")
    private String codigoPostalDirreccionPpal;

}
