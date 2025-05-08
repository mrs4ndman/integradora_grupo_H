package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.auxiliar.EntidadBancaria;
import org.grupo_h.empleados.dto.EntidadBancariaDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface EntidadBancariaService {
    List<EntidadBancaria> listarEntidadBancaria();

    Optional<EntidadBancariaDTO> findByNombreEntidad(String nombreEntidad);

    List<EntidadBancariaDTO> obtenerEntidadesBancarias();
}
