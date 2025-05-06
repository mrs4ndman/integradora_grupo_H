package org.grupo_h.administracion.service;

import org.grupo_h.administracion.dto.EmpleadoDetalleDTO;
import org.grupo_h.comun.repository.EmpleadoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementación del servicio de empleados.
 * Proporciona métodos para gestionar la información de los empleados.
 */
@Service
public class EmpleadoServiceImpl implements EmpleadoService {

    @Autowired
    private final EmpleadoRepository empleadosRepository;

    /**
     * Constructor para la inyección de dependencias.
     *
     * @param empleadosRepository Repositorio de empleados
     */
    public EmpleadoServiceImpl(EmpleadoRepository empleadosRepository) {
        this.empleadosRepository = empleadosRepository;
    }

    /**
     * Obtiene los detalles de un empleado por su ID.
     *
     * @param id Identificador único del empleado, tipo {@link UUID}
     * @return {@link Optional} con los detalles del empleado si existe, o empty si no existe
     */
    @Override
    public Optional<EmpleadoDetalleDTO> obtenerDetalleEmpleado(UUID id) {
        ModelMapper modelMapper = new ModelMapper();
        return empleadosRepository.findById(id)
                .map(empleado -> modelMapper.map(empleado, EmpleadoDetalleDTO.class));
    }

}
