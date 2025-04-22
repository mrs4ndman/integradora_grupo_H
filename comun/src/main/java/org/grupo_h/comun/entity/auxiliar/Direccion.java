package org.grupo_h.comun.entity.auxiliar;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
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
    @Column(name = "tipo_via")
    private String tipoVia;

    /** Nombre de la vía. */
    @Column(name = "via")
    private String via;

    /** Número de la dirección. */
    @Column(name = "numero")
    private int numero;

    /** Piso del edificio. */
    @Column(name = "piso")
    private int piso;

    /** Puerta o letra del piso. */
    @Column(name = "puerta")
    private String puerta;

    /** Localidad o ciudad. */
    @Column(name = "localidad")
    private String localidad;

    /** Código postal. */
    @Column(name = "codigo_postal")
    private String codigoPostal;

    /** Región o provincia. */
    @Column(name = "region")
    private String region;

    /** País. */
    @Column(name = "pais")
    private String pais;
}
