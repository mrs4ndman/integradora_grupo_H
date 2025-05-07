package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.comun.entity.Departamento;
import org.grupo_h.comun.entity.auxiliar.*;
import org.grupo_h.comun.repository.*;
import org.grupo_h.empleados.dto.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmpleadoServiceImpl implements EmpleadoService {

    private final EmpleadoRepository empleadosRepository;
    public final EntidadBancariaRepository entidadBancariaRepository;
    private final UsuarioRepository usuarioRepository;
    private final GeneroRepository generoRepository;
    private final TipoTarjetaCreditoRepository tipoTarjetaCreditoRepository;
    private final DepartamentoRepository departamentoRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public EmpleadoServiceImpl(EmpleadoRepository empleadosRepository,
                               UsuarioRepository usuarioRepository,
                               GeneroRepository generoRepository,
                               ModelMapper modelMapper, AbstractConfigurableTemplateResolver abstractConfigurableTemplateResolver, EntidadBancariaRepository entidadBancariaRepository, TipoTarjetaCreditoRepository tipoTarjetaCreditoRepository, DepartamentoRepository departamentoRepository) {
        this.empleadosRepository = empleadosRepository;
        this.usuarioRepository = usuarioRepository;
        this.generoRepository = generoRepository;
        this.modelMapper = modelMapper;
        this.entidadBancariaRepository = entidadBancariaRepository;
        this.tipoTarjetaCreditoRepository = tipoTarjetaCreditoRepository;
        this.departamentoRepository = departamentoRepository;
    }

    @Override
    @Transactional
    public Empleado registrarEmpleado(EmpleadoRegistroDTO empleadoDTO) {
        Empleado empleado = modelMapper.map(empleadoDTO, Empleado.class);

        if (empleado.getTarjetas() != null && empleado.getTarjetas().getTipoTarjetaCredito() != null) {
            String nombreTipoTarjeta = empleado.getTarjetas().getTipoTarjetaCredito().getNombreTipoTarjeta();
            if (nombreTipoTarjeta != null && !nombreTipoTarjeta.isEmpty()) {
                Optional<TipoTarjetaCredito> tipoExistenteOpt = tipoTarjetaCreditoRepository.findByNombreTipoTarjeta(nombreTipoTarjeta);

                if (tipoExistenteOpt.isPresent()) {
                    empleado.getTarjetas().setTipoTarjetaCredito(tipoExistenteOpt.get());
                } else {
                    // Opción 1: Crear nuevo tipo si no existe (y si la lógica de negocio lo permite)
                    // TipoTarjetaCredito nuevoTipo = new TipoTarjetaCredito();
                    // nuevoTipo.setNombreTipoTarjeta(nombreTipoTarjeta);
                    // tipoTarjetaCreditoRepository.save(nuevoTipo); // Guardar primero
                    // empleado.getTarjetas().setTipoTarjetaCredito(nuevoTipo);

                    // Opción 2: Lanzar error si el tipo debe preexistir
                    throw new RuntimeException("TipoTarjetaCredito no encontrado con nombre: " + nombreTipoTarjeta + " y no se permite la creación automática.");
                }
            } else if (empleado.getTarjetas().getTipoTarjetaCredito() != null) { // Si el objeto existe pero sin nombre
                    throw new RuntimeException("Se proporcionó una tarjeta de crédito pero su tipo es nulo o vacío.");
            }
        }
        // Aquí puedes añadir lógica similar para EntidadBancaria si es necesario,
        // basándote en cómo se maneje en tu DTO y entidad.

        // --- INICIO: NUEVA LÓGICA PARA GESTIONAR DEPARTAMENTO ---
        System.out.println(empleadoDTO.getDepartamentoDTO());
        if (empleadoDTO.getDepartamentoDTO() != null && empleadoDTO.getDepartamentoDTO().getId() != null) {
            UUID departamentoId = empleadoDTO.getDepartamentoDTO().getId();
            Departamento departamentoPersistente = departamentoRepository.findById(departamentoId)
                    .orElseThrow(() -> new RuntimeException("Departamento no encontrado con ID: " + departamentoId + ". El departamento seleccionado no es válido."));
            empleado.setDepartamento(departamentoPersistente);
        } else if (empleadoDTO.getDepartamentoDTO() != null && (empleadoDTO.getDepartamentoDTO().getId() == null && empleadoDTO.getDepartamentoDTO().getNombreDept() != null)) {

            throw new RuntimeException("Se proporcionó información de departamento sin un ID válido. Por favor, seleccione un departamento existente.");
        } else if (empleadoDTO.getDepartamentoDTO() == null && empleado.getDepartamento() != null) {

            throw new RuntimeException("No se proporcionó información del departamento, pero es requerida.");
        }
        // --- FIN: NUEVA LÓGICA PARA GESTIONAR DEPARTAMENTO ---

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