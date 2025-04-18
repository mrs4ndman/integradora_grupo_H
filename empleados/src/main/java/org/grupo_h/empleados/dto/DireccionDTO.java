package org.grupo_h.empleados.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DireccionDTO {

    @NotBlank
    private String tipoVia;

    @NotBlank
    private String via;

    @NotNull
    @Positive(message = "{Validadcion.direccionDTO.numeroPositivo}")
    private Integer numero;

    @NotNull(message = "{Validadcion.direccionDTO.piso}")
    private Integer piso;

    @NotBlank(message = "{Validadcion.direccionDTO.puerta}")
    private String puerta;

    @NotBlank(message = "{Validadcion.direccionDTO.localidad}")
    private String localidad;

    @NotBlank(message = "{Validadcion.direccionDTO.codigoPostal}")
    private String codigoPostal;

    @NotBlank(message = "{Validadcion.direccionDTO.region}")
    private String region;

    @NotBlank(message = "{Validadcion.direccionDTO.pais}")
    private String pais;
}
