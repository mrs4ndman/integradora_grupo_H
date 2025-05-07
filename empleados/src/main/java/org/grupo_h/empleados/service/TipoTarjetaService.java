package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.auxiliar.TipoTarjetaCredito;
import org.grupo_h.empleados.dto.TipoTarjetaCreditoDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface TipoTarjetaService {
    List<TipoTarjetaCredito> listarTipoTarjetaCredito();

    Optional<Object> findByNombre(TipoTarjetaCreditoDTO tipoTarjetaCodigo);

    Optional<Object> findByNombre(String tipoTarjetaNombre);
}
