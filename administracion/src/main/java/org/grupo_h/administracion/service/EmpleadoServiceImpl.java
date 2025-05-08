package org.grupo_h.administracion.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.grupo_h.administracion.dto.EmpleadoDetalleDTO;
import org.grupo_h.administracion.dto.EmpleadoSimpleDTO;
import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.comun.repository.EmpleadoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de empleados.
 * Proporciona métodos para gestionar la información de los empleados.
 */
@Service
public class EmpleadoServiceImpl implements EmpleadoService {

    @Autowired
    private final EmpleadoRepository empleadosRepository;
    @Autowired
    private final ModelMapper modelMapper;

    /**
     * Constructor para la inyección de dependencias.
     *
     * @param empleadosRepository Repositorio de empleados
     */
    public EmpleadoServiceImpl(EmpleadoRepository empleadosRepository, ModelMapper modelMapper) {
        this.empleadosRepository = empleadosRepository;
        this.modelMapper = modelMapper;
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

    @Override
    @Transactional
    public void asignarSubordinado(UUID idJefePropuesto, UUID idSubordinado) {
        if (idJefePropuesto.equals(idSubordinado)) {
            throw new IllegalArgumentException("Un empleado no puede ser su propio jefe o subordinado.");
        }

        Empleado jefePropuesto = empleadosRepository.findById(idJefePropuesto)
                .orElseThrow(() -> new EntityNotFoundException("Jefe propuesto no encontrado con ID: " + idJefePropuesto));

        Empleado subordinado = empleadosRepository.findById(idSubordinado)
                .orElseThrow(() -> new EntityNotFoundException("Subordinado no encontrado con ID: " + idSubordinado));

        // No se puede asignar como jefe a alguien que ya es tu subordinado
        Empleado jefeActualDelJefePropuesto = jefePropuesto.getJefe();
        while (jefeActualDelJefePropuesto != null) {
            if (jefeActualDelJefePropuesto.getId().equals(idSubordinado)) {
                throw new IllegalArgumentException("Asignación cíclica detectada: " +
                        subordinado.getNombre() + " " + subordinado.getApellidos() +
                        " ya es un superior (directo o indirecto) de " +
                        jefePropuesto.getNombre() + " " + jefePropuesto.getApellidos() +
                        ". No se puede asignar como jefe.");
            }
            jefeActualDelJefePropuesto = jefeActualDelJefePropuesto.getJefe();
        }

        if (subordinado.getJefe() != null && !subordinado.getJefe().getId().equals(jefePropuesto.getId())) {
            Empleado jefeAnterior = subordinado.getJefe();
        }


        jefePropuesto.addSubordinado(subordinado);
        empleadosRepository.save(jefePropuesto);
    }

    @Override
    @Transactional
    public void desasignarSubordinadoDeSuJefe(UUID idSubordinado) {
        Empleado subordinado = empleadosRepository.findById(idSubordinado)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con ID: " + idSubordinado));

        Empleado jefeActual = subordinado.getJefe();

        if (jefeActual == null) {
            throw new IllegalStateException("El empleado " + subordinado.getApellidos() + ", " + subordinado.getNombre() + " no tiene un jefe asignado.");
        }

        jefeActual.removeSubordinado(subordinado);
        empleadosRepository.save(jefeActual);
    }

    @Override
    @Transactional
    public List<EmpleadoSimpleDTO> obtenerTodosLosEmpleadosParaSeleccion() {
        return empleadosRepository.findAll().stream()
                .map(empleado -> {
                    EmpleadoSimpleDTO dto = new EmpleadoSimpleDTO();
                    dto.setId(empleado.getId());
                    dto.setNombre(empleado.getNombre());
                    dto.setApellidos(empleado.getApellidos());
                    if (empleado.getJefe() != null) {
                        dto.setNombreJefe(empleado.getJefe().getApellidos() + ", " + empleado.getJefe().getNombre());
                    } else {
                        dto.setNombreJefe("- Sin Jefe -");
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

}
