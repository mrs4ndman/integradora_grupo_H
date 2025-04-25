package org.grupo_h.empleados.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String tipoViaDTO;

    /**
     * Nombre de la vía. No puede estar vacío.
     */
    @NotBlank
    private String nombreViaDTO;

    /**
     * Número de la dirección. Debe ser un valor positivo.
     */
    @NotNull
    @Positive(message = "{Validadcion.direccionDTO.numeroPositivo}")
    private Integer numeroViaDTO;

    /**
     * Portal del edificio. No puede ser nulo.
     */
    @NotNull(message = "{Validadcion.direccionDTO.piso}")
    private String portalDTO;

    /**
     * Planta del edificio. No puede ser nulo.
     */
    @NotNull(message = "{Validadcion.direccionDTO.planta}")
    private String plantaDTO;

    /**
     * Puerta o letra del piso. No puede estar vacío.
     */
    @NotBlank(message = "{Validadcion.direccionDTO.puerta}")
    private String puertaDTO;

    /**
     * Localidad o ciudad. No puede estar vacío.
     */
    @NotBlank(message = "{Validadcion.direccionDTO.localidad}")
    private String localidadDTO;


    /**
     * Región, Comunidad Autonoma o Estado. No puede estar vacío.
     */
    @NotBlank(message = "{Validadcion.direccionDTO.region}")
    private String regionDTO;

    /**
     * Código postal. No puede estar vacío.
     */
    @NotBlank(message = "{Validadcion.direccionDTO.codigoPostal}")
    private String codigoPostalDTO;
}
