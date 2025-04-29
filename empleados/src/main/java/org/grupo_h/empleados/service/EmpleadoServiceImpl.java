package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.DatosEconomicos;
import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.comun.entity.auxiliar.*;
import org.grupo_h.empleados.dto.*;
import org.grupo_h.comun.repository.EmpleadoRepository;
import org.modelmapper.ModelMapper;
import org.grupo_h.comun.repository.GeneroRepository;
import org.grupo_h.comun.repository.UsuarioRepository;
import org.grupo_h.comun.entity.auxiliar.Genero;
import org.modelmapper.ModelMapper;
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

//        Direccion direccion = new Direccion();
//        DireccionDTO direccionDTO = new DireccionDTO();
//        direccion = modelMapper.map(direccionDTO, Direccion.class);
//
//        empleado.setDireccion(direccion);
//
//        CuentaCorriente cuentaCorriente = new CuentaCorriente();
//        cuentaCorriente = modelMapper.map(cuentaCorriente, CuentaCorriente.class);
//
//        DatosEconomicos datosEconomicos = new DatosEconomicos();
//        datosEconomicos.setCuentaCorriente(cuentaCorriente);
//
//        empleado.setDatosEconomicos(datosEconomicos);
//
//        TarjetaCredito tarjetaCredito = new TarjetaCredito();
//        TarjetaCreditoDTO tarjetaCreditoDTO = new TarjetaCreditoDTO();
//        tarjetaCredito = modelMapper.map(empleadoDTO, TarjetaCredito.class);


        // Persistir el empleado
        return empleadosRepository.save(empleado);
    }



    /**
     * Busca un empleado por su nombre.
     *
     * @param nombreEmpleado Nombre del empleado a buscar.
     * @return Un Optional con el empleado encontrado, si existe.
     */
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
//                    detalleDTO.setCuentaCorriente(empleado.getDatosEconomicos().getCuentaCorriente());

                    // Mapear el género
                    if (empleado.getGenero() != null) {
                        detalleDTO.setGenero(empleado.getGenero());
                    }
                    return detalleDTO;
                });
    }
}