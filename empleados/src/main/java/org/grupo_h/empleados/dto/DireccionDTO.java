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
    private String tipoVia;

    /**
     * Nombre de la vía. No puede estar vacío.
     */
    @NotBlank
    private String via;

    /**
     * Número de la dirección. Debe ser un valor positivo.
     */
    @NotNull
    @Positive(message = "{Validadcion.direccionDTO.numeroPositivo}")
    private Integer numero;

    /**
     * Piso del edificio. No puede ser nulo.
     */
    @NotNull(message = "{Validadcion.direccionDTO.piso}")
    private Integer piso;

    /**
     * Puerta o letra del piso. No puede estar vacío.
     */
    @NotBlank(message = "{Validadcion.direccionDTO.puerta}")
    private String puerta;

    /**
     * Localidad o ciudad. No puede estar vacío.
     */
    @NotBlank(message = "{Validadcion.direccionDTO.localidad}")
    private String localidad;

    /**
     * Código postal. No puede estar vacío.
     */
    @NotBlank(message = "{Validadcion.direccionDTO.codigoPostal}")
    private String codigoPostal;

    /**
     * Región o provincia. No puede estar vacío.
     */
    @NotBlank(message = "{Validadcion.direccionDTO.region}")
    private String region;

    /**
     * País. No puede estar vacío.
     */
    @NotBlank(message = "{Validadcion.direccionDTO.pais}")
    private String pais;
}
