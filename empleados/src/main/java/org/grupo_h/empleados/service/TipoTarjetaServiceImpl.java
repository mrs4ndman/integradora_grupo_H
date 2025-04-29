package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.auxiliar.TipoTarjetaCredito;
import org.grupo_h.comun.repository.TipoTarjetaCreditoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class TipoTarjetaServiceImpl implements TipoTarjetaService {

    @Autowired
    private TipoTarjetaCreditoRepository tipoTarjetaCreditoRepository;

    @Override
    public List<TipoTarjetaCredito> listarTipoTarjetaCredito() {
        return tipoTarjetaCreditoRepository.findAll();
    }
}
