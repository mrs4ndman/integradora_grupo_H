package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.comun.entity.auxiliar.*;
import org.grupo_h.comun.repository.EntidadBancariaRepository;
import org.grupo_h.empleados.dto.*;
import org.grupo_h.comun.repository.EmpleadoRepository;
import org.modelmapper.ModelMapper;
import org.grupo_h.comun.repository.GeneroRepository;
import org.grupo_h.comun.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;

import java.util.Optional;
import java.util.UUID;

@Service
public class EmpleadoServiceImpl implements EmpleadoService {

    private final EmpleadoRepository empleadosRepository;
    public final EntidadBancariaRepository entidadBancariaRepository;
    private final UsuarioRepository usuarioRepository;
    private final GeneroRepository generoRepository;
    private final EntidadBancariaServiceImpl tipoTarjetaCreditoRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public EmpleadoServiceImpl(EmpleadoRepository empleadosRepository,
                               UsuarioRepository usuarioRepository,
                               GeneroRepository generoRepository,
                               ModelMapper modelMapper, AbstractConfigurableTemplateResolver abstractConfigurableTemplateResolver, EntidadBancariaRepository entidadBancariaRepository, EntidadBancariaServiceImpl tipoTarjetaCreditoRepository) {
        this.empleadosRepository = empleadosRepository;
        this.usuarioRepository = usuarioRepository;
        this.generoRepository = generoRepository;
        this.modelMapper = modelMapper;
        this.entidadBancariaRepository = entidadBancariaRepository;
        this.tipoTarjetaCreditoRepository = tipoTarjetaCreditoRepository;
    }

    @Override
    @Transactional
    public Empleado registrarEmpleado(EmpleadoRegistroDTO empleadoDTO) {
        Empleado empleado = modelMapper.map(empleadoDTO, Empleado.class);

//        if (empleado.getCuentaCorriente() != null && empleado.getCuentaCorriente().getEntidadBancaria() != null) {
//            Long entidadBancariaId = empleado.getCuentaCorriente().getEntidadBancaria().getId();
//            if (entidadBancariaId != null) {
//                EntidadBancaria entidadBancaria = entidadBancariaRepository.findById(entidadBancariaId)
//                        .orElseThrow(() -> new RuntimeException("EntidadBancaria no encontrada con id: " + entidadBancariaId));
//                empleado.getCuentaCorriente().setEntidadBancaria(entidadBancaria);
//            } else {
//                throw new RuntimeException("EntidadBancaria ID es null");
//            }
//        }
//
//        if (empleado.getTarjetas().getTipoTarjetaCredito() != null) {
//            Long tipoTarjetaId = empleado.getTarjetas().getTipoTarjetaCredito().getId();
//            if (tipoTarjetaId != null) {
//                TipoTarjetaCredito tipoTarjetaCredito = (TipoTarjetaCredito) tipoTarjetaCreditoRepository.findById(tipoTarjetaId)
//                        .orElseThrow(() -> new RuntimeException("TipoTarjetaCredito no encontrada con id: " + tipoTarjetaId));
//                empleado.getTarjetas().setTipoTarjetaCredito(tipoTarjetaCredito);
//            }
//        }

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