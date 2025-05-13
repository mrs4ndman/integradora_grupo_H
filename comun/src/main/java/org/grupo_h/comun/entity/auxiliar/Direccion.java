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

    public String toStringBonito() {
        StringBuilder sb = new StringBuilder();
        if (tipoViaDireccionPpal != null && nombreViaDireccionPpal != null) {
            sb.append(tipoViaDireccionPpal.getTipoVia())
                    .append(" ")
                    .append(nombreViaDireccionPpal);
        }

        if (numeroViaDireccionPpal != null) {
            sb.append(" | Nº ").append(numeroViaDireccionPpal);
        }

        if (portalDireccionPpal != null && !portalDireccionPpal.isEmpty()) {
            sb.append(" | Portal: ").append(portalDireccionPpal);
        }

        if (plantaDireccionPpal != null) {
            sb.append(" | Planta: ").append(plantaDireccionPpal);
        }

        if (puertaDireccionPpal != null && !puertaDireccionPpal.isEmpty()) {
            sb.append(" | Puerta: ").append(puertaDireccionPpal);
        }

        if (codigoPostalDireccionPpal != null && !codigoPostalDireccionPpal.isEmpty()) {
            sb.append(" | ").append(codigoPostalDireccionPpal);
        }

        if (localidadDireccionPpal != null && !localidadDireccionPpal.isEmpty()) {
            sb.append(" | ").append(localidadDireccionPpal);
        }

        if (regionDireccionPpal != null && !regionDireccionPpal.isEmpty()) {
            sb.append(" | ").append(regionDireccionPpal);
        }

        return sb.toString();
    }
}
