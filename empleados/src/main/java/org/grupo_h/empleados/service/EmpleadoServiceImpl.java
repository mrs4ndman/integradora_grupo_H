package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.empleados.dto.EmpleadoDetalleDTO;
import org.grupo_h.empleados.dto.EmpleadoRegistroDTO;
import org.grupo_h.comun.repository.EmpleadoRepository;
import org.modelmapper.ModelMapper;
import org.grupo_h.comun.repository.GeneroRepository;
import org.grupo_h.comun.repository.UsuarioRepository;
import org.grupo_h.comun.entity.auxiliar.Genero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class EmpleadoServiceImpl implements EmpleadoService {

    private final EmpleadoRepository empleadosRepository;
    private final UsuarioRepository usuarioRepository;
    private final GeneroRepository generoRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public EmpleadoServiceImpl(EmpleadoRepository empleadosRepository,
                               UsuarioRepository usuarioRepository,
                               GeneroRepository generoRepository,
                               ModelMapper modelMapper) {
        this.empleadosRepository = empleadosRepository;
        this.usuarioRepository = usuarioRepository;
        this.generoRepository = generoRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public Empleado registrarEmpleado(EmpleadoRegistroDTO empleadoDTO) {
        Empleado empleado = modelMapper.map(empleadoDTO, Empleado.class);

        // Buscar el Género por su código y asignarlo al empleado
        Genero genero = (Genero) generoRepository.findByCodigoGenero(empleadoDTO.getGenero())
                .orElseThrow(() -> new IllegalArgumentException("Género no encontrado: " + empleadoDTO.getGenero()));
        empleado.setGenero(genero);

        // Persistir el empleado
        return empleadosRepository.save(empleado);
    }

    @Override
    public Optional<Empleado> findByNombreEmpleado(String nombreEmpleado) {
        return empleadosRepository.findByNombre(nombreEmpleado);
    }

    @Override
    public Optional<EmpleadoDetalleDTO> obtenerDetalleEmpleado(UUID id) {
        return empleadosRepository.findById(id)
                .map(empleado -> {
                    EmpleadoDetalleDTO detalleDTO = new EmpleadoDetalleDTO();
                    detalleDTO.setNombre(empleado.getNombre());
                    detalleDTO.setApellidos(empleado.getApellidos());
                    detalleDTO.setFechaNacimiento(empleado.getFechaNacimiento());
                    detalleDTO.setDireccion(empleado.getDireccion());
                    detalleDTO.setCuentaCorriente(empleado.getCuentaCorriente());

                    // Mapear el género
                    if (empleado.getGenero() != null) {
                        detalleDTO.setGenero(empleado.getGenero());
                    }
                    return detalleDTO;
                });
    }
}