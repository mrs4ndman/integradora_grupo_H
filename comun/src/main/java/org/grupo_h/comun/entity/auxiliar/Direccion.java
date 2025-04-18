package org.grupo_h.comun.entity.auxiliar;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Embeddable
public class Direccion {

    @Column(name = "tipo_via")
    private String tipoVia;

    @Column(name = "via")
    private String via;

    @Column(name = "numero")
    private int numero;

    @Column(name = "piso")
    private int piso;

    @Column(name = "puerta")
    private String puerta;

    @Column(name = "localidad")
    private String localidad;

    @Column(name = "codigo_postal")
    private String codigoPostal;

    @Column(name = "region")
    private String region;

    @Column(name = "pais")
    private String pais;
}

