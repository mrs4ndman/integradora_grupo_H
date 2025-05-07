package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.auxiliar.TipoTarjetaCredito;
import org.grupo_h.comun.repository.TipoTarjetaCreditoRepository;
import org.grupo_h.empleados.dto.TipoTarjetaCreditoDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipoTarjetaServiceImpl implements TipoTarjetaService {

    @Autowired
    private TipoTarjetaCreditoRepository tipoTarjetaCreditoRepository;

    private final ModelMapper modelMapper;

    public TipoTarjetaServiceImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public List<TipoTarjetaCredito> listarTipoTarjetaCredito() {
        return tipoTarjetaCreditoRepository.findAll();
    }

    @Override
    public Optional<Object> findByNombre(TipoTarjetaCreditoDTO tipoTarjetaCodigo) {
        return Optional.empty();
    }

    @Override
    public Optional<Object> findByNombre(String tipoTarjetaNombre) {
        return tipoTarjetaCreditoRepository.findAll().stream()
                .filter(t -> t.getNombreTipoTarjeta().equalsIgnoreCase(tipoTarjetaNombre))
                .findFirst()
                .map(t -> modelMapper.map(t, TipoTarjetaCreditoDTO.class));
    }
}
