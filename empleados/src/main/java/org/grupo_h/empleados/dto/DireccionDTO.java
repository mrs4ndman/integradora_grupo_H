package org.grupo_h.empleados.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.grupo_h.empleados.Validaciones.GruposValidaciones.DatosPersonales;
import org.grupo_h.empleados.Validaciones.GruposValidaciones.DatosRegistroDireccion;

/**
 * DTO que representa los datos de una dirección.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DireccionDTO {

    /**
     * Tipo de vía (calle, avenida, etc.). No puede estar vacío.
     */
    @NotBlank
    private String tipoViaDirreccionPpalDTO = "C";

    /**
     * Nombre de la vía. No puede estar vacío.
     */
    @NotBlank
    private String nombreViaDirreccionPpalDTO;

    /**
     * Número de la dirección. Debe ser un valor positivo.
     */
    @NotNull(groups = DatosRegistroDireccion.class)
    @Positive(message = "{Validadcion.direccionDTO.numeroPositivo}")
    private Integer numeroViaDirreccionPpalDTO;

    /**
     * Portal del edificio. No puede ser nulo.
     */
    @NotNull(message = "{Validadcion.direccionDTO.piso}",groups = DatosRegistroDireccion.class)
    private String portalDirreccionPpalDTO;

    /**
     * Planta del edificio. No puede ser nulo.
     */
    @NotNull(message = "{Validadcion.direccionDTO.planta}" ,groups = DatosRegistroDireccion.class)
    private String plantaDirreccionPpalDTO;

    /**
     * Puerta o letra del piso. No puede estar vacío.
     */
    @NotBlank(message = "{Validadcion.direccionDTO.puerta}", groups = DatosRegistroDireccion.class)
    private String puertaDirreccionPpalDTO;

    /**
     * Localidad o ciudad. No puede estar vacío.
     */
    @NotBlank(message = "{Validadcion.direccionDTO.localidad}", groups = DatosRegistroDireccion.class)
    private String localidadDirreccionPpalDTO;


    /**
     * Región, Comunidad Autonoma o Estado. No puede estar vacío.
     */
//    @NotBlank(message = "{Validadcion.direccionDTO.region}")
    private String regionDirreccionPpalDTO;

    /**
     * Código postal. No puede estar vacío.
     */
    @NotBlank(message = "{Validadcion.direccionDTO.codigoPostal}", groups = DatosRegistroDireccion.class)
    private String codigoPostalDirreccionPpalDTO;
}
