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

    /**
     * Tipo de vía (calle, avenida, etc.).
     */
    @ManyToOne
    @JoinColumn(name = "tipo_via_direccion_ppal_id",
            referencedColumnName = "codigoTipoVia",
            foreignKey = @ForeignKey(name = "FK_TIPOVIA_DIRECCION"))
    private TipoVia tipoViaDireccionPpal;

    /**
     * Nombre de la vía.
     */
    @Column(name = "nombre_via")
    private String nombreViaDireccionPpal;

    /**
     * Número de la dirección.
     */
    @Column(name = "numero")
    private int numeroViaDireccionPpal;

    /**
     * Portal de la dirección.
     */
    @Column(name = "portal")
    private String portalDireccionPpal;

    /**
     * Planta de la dirección.
     */
    @Column(name = "planta")
    private String plantaDireccionPpal;

    /**
     * Puerta de la dirección.
     */
    @Column(name = "puerta")
    private String puertaDireccionPpal;

    /**
     * Localidad o ciudad.
     */
    @Column(name = "localidad")
    private String localidadDireccionPpal;

    /**
     * Región/Comunidad Autónoma o Estado.
     */
    @Column(name = "region")
    private String regionDireccionPpal;

    /**
     * Código postal.
     */
    @Column(name = "cod_postal")
    private String codigoPostalDireccionPpal;

}
