package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.auxiliar.TipoTarjetaCredito;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface TipoTarjetaService {
    List<TipoTarjetaCredito> listarTipoTarjetaCredito();
}
