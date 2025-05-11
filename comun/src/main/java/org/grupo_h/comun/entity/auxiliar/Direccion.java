package org.grupo_h.comun.entity.auxiliar;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa una dirección postal.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Direccion {

    /**
     * Tipo de vía (calle, avenida, etc.).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_via_direccion_ppal_id",
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
    private Integer numeroViaDireccionPpal;

    /**
     * Portal de la dirección.
     */
    @Column(name = "portal")
    private String portalDireccionPpal;

    /**
     * Planta de la dirección.
     */
    @Column(name = "planta")
    private Integer plantaDireccionPpal;

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
