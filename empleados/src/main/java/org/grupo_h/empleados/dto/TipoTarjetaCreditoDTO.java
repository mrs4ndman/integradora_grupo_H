package org.grupo_h.empleados.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link org.grupo_h.comun.entity.auxiliar.TipoTarjetaCredito}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TipoTarjetaCreditoDTO implements Serializable {
    UUID id;
    String codigoTipoTarjetaCredito;
    String tipoTarjetaCredito;
}