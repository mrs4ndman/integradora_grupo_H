package org.grupo_h.empleados.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.grupo_h.empleados.Validaciones.GruposValidaciones.DatosFinales;
import org.grupo_h.empleados.Validaciones.GruposValidaciones.DatosFinancieros;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntidadBancariaDTO {

//    private UUID id;

//    @NotNull(message = "{Validacion.entidadBancaria.NotNull}", groups = DatosFinancieros.class)
    private String nombreEntidadDTO;
}
