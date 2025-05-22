package org.grupo_h.administracion.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.grupo_h.administracion.dto.EmpleadoConsultaDTO;
import org.grupo_h.administracion.dto.EmpleadoDTO;
import org.grupo_h.administracion.dto.EmpleadoDetalleDTO;
import org.grupo_h.administracion.dto.EmpleadoSimpleDTO;
import org.grupo_h.administracion.specs.EmpleadoSpecs;
import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.comun.entity.Usuario;
import org.grupo_h.comun.repository.EmpleadoRepository;
import org.grupo_h.comun.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
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
    private final UsuarioRepository usuarioRepository;
    @Autowired
    private final ModelMapper modelMapper;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Constructor para la inyección de dependencias.
     *
     * @param empleadosRepository Repositorio de empleados
     */
    public EmpleadoServiceImpl(EmpleadoRepository empleadosRepository, UsuarioRepository usuarioRepository, ModelMapper modelMapper) {
        this.empleadosRepository = empleadosRepository;
        this.usuarioRepository = usuarioRepository;
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

//    @Override
//    @Transactional
//    public List<EmpleadoDTO> buscarEmpleados(EmpleadoConsultaDTO filtro) {
//        Specification<Empleado> spec = Specification.where(EmpleadoSpecs.esActivo());
//
//        if (filtro.getNombreDTO() != null && !filtro.getNombreDTO().trim().isEmpty()) {
//            spec = spec.and(EmpleadoSpecs.nombreContiene(filtro.getNombreDTO().trim()));
//        }
//
//        if (filtro.getNumeroDni() != null && !filtro.getNumeroDni().trim().isEmpty()) {
//            spec = spec.and(EmpleadoSpecs.numeroDocumentoContiene(filtro.getNumeroDni().trim()));
//        }
//
//        if (filtro.getEdadMin() != null && filtro.getEdadMax() != null) {
//            Specification<Empleado> edadSpec = EmpleadoSpecs.edadEntre(filtro.getEdadMin(), filtro.getEdadMax());
//            if (edadSpec != null) {
//                spec = spec.and(edadSpec);
//            }
//        }
//
//        if (filtro.getDepartamentosDTO() != null && !filtro.getDepartamentosDTO().isEmpty()) {
//            spec = spec.and(EmpleadoSpecs.departamentosEnLista(filtro.getDepartamentosDTO()));
//        }
//
//        Sort sort = Sort.by("apellidos").ascending().and(Sort.by("nombre").ascending());
//        List<Empleado> empleados = empleadosRepository.findAll(spec, sort);
//
//        return empleados.stream()
//                .map(e -> {
//                    String fotoB64 = "";
//                    if (e.getFotografia() != null && e.getFotografia().length > 0) {
//                        fotoB64 = "data:image/jpeg;base64," +
//                                Base64.getEncoder().encodeToString(e.getFotografia());
//                    }
//                    return new EmpleadoDTO(
//                            e.getId(),
//                            e.getNombre(),
//                            e.getApellidos(),
//                            e.getEdad(),
//                            e.getDepartamento().getNombreDept(),
//                            e.getNumeroDocumento(),
//                            fotoB64,
//                            null
//                    );
//                })
//                .collect(Collectors.toList());
//    }

    @Override
    public Page<EmpleadoDTO> buscarEmpleadosPaginados(EmpleadoConsultaDTO filtro,
                                                      int pagina,
                                                      int tamaño) {
        // 1) Construir la Specification como antes
        Specification<Empleado> spec = Specification.where(null);

        if (filtro.getNombreDTO() != null && !filtro.getNombreDTO().isBlank()) {
            spec = spec.and(EmpleadoSpecs.nombreContiene(filtro.getNombreDTO()));
        }
        if (filtro.getEdadMin() != null || filtro.getEdadMax() != null) {
            spec = spec.and(EmpleadoSpecs.edadEntre(filtro.getEdadMin(), filtro.getEdadMax()));
        }

        if (filtro.getDepartamentosDTO() != null && !filtro.getDepartamentosDTO().isEmpty()) {
            spec = spec.and(EmpleadoSpecs.departamentosEnLista(filtro.getDepartamentosDTO()));
        }

        if (filtro.getNumeroDni() != null && !filtro.getNumeroDni().isBlank()) {
            spec = spec.and(EmpleadoSpecs.numeroDocumentoContiene(filtro.getNumeroDni()));
        }

        // 2) Crear Pageable con orden por nombre
        Pageable pageable = PageRequest.of(pagina, tamaño, Sort.by("nombre").ascending());

        // 3) Llamar al repo con spec + pageable
        Page<Empleado> pageEntidades = empleadosRepository.findAll(spec, pageable);

        // 4) Mapear cada Empleado → EmpleadoDTO
        Page<EmpleadoDTO> pageDTOs = pageEntidades.map(e -> {
            String fotoB64 = "";
            if (e.getFotografia() != null && e.getFotografia().length > 0) {
                fotoB64 = "data:image/jpeg;base64," +
                        Base64.getEncoder().encodeToString(e.getFotografia());
            }
            return new EmpleadoDTO(
                    e.getId(),
                    e.getNombre(),
                    e.getApellidos(),
                    e.getEdad(),
                    e.getDepartamento() != null ? e.getDepartamento().getNombreDept() : null,
                    e.getNumeroDocumento(),
                    fotoB64,
                    null,
                    e.isActivo()
            );
        });

        return pageDTOs;
    }


    @Override
    public Optional<Empleado> buscarPorDni(String dni) {
        return empleadosRepository.findByNumeroDocumentoAndActivoTrue(dni);
    }

    @Override
    public Optional<Empleado> buscarEmpleado(UUID empleadoID) {
        return empleadosRepository.findById(empleadoID);
    }

    @Transactional
    @Override
    public void eliminarPorId(UUID id) {
        if (!empleadosRepository.existsById(id)) {
            throw new EntityNotFoundException("Empleado no existe con id " + id);
        }
        empleadosRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void eliminarPorDni(UUID id) {
        Optional<Empleado> empleadoOpt = empleadosRepository.findById(id);
        if (empleadoOpt.isEmpty()) {
            throw new EntityNotFoundException("Empleado no existe con id  " + id);
        }
        try {
            empleadosRepository.delete(empleadoOpt.get());
            log.warn("'Empleado borrado correctamente ' {}", empleadoOpt.get() );
            System.out.println("Empleado borrado correctamente");
        }catch (Exception e){
            log.warn("'Excepcion al borrar Empleado ' {}", e.getMessage());
            System.out.println("Empleado no borrado");
        }
    }

    @Override
    @Transactional
    public Page<EmpleadoDTO> getEmpleadosParaGestionEstado(String searchTerm, Pageable pageable) {
        Page<Empleado> paginaEmpleados;
        if (StringUtils.hasText(searchTerm)) {
            paginaEmpleados = empleadosRepository.findByNombreContainingIgnoreCaseOrApellidosContainingIgnoreCaseOrNumeroDocumentoContainingIgnoreCase(
                    searchTerm, searchTerm, searchTerm, pageable
            );
        } else {
            // Muestra todos los empleados (activos e inactivos)
            paginaEmpleados = empleadosRepository.findAll(pageable);
        }

        List<EmpleadoDTO> dtos = paginaEmpleados.getContent().stream()
                .map(this::convertirAEmpleadoDTOConEstado)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, paginaEmpleados.getTotalElements());
    }

    @Override
    @Transactional
    public void darDeBajaLogicaEmpleado(UUID empleadoId) {
        Empleado empleado = empleadosRepository.findById(empleadoId)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con ID: " + empleadoId));
        empleado.setActivo(false);
        empleadosRepository.save(empleado);

        // También deshabilitar el usuario asociado para que no pueda iniciar sesión
        Usuario usuario = empleado.getUsuario();
        if (usuario != null) {
            usuario.setHabilitado(false);
            usuarioRepository.save(usuario);
        }
    }

    @Override
    @Transactional
    public void reactivarEmpleado(UUID empleadoId) {
        Empleado empleado = empleadosRepository.findById(empleadoId)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con ID: " + empleadoId));
        empleado.setActivo(true);
        empleadosRepository.save(empleado);

        // También habilitar el usuario asociado
        Usuario usuario = empleado.getUsuario();
        if (usuario != null) {
            usuario.setHabilitado(true);
            usuarioRepository.save(usuario);
        }
    }

    private EmpleadoDTO convertirAEmpleadoDTOConEstado(Empleado empleado) {
        EmpleadoDTO dto = modelMapper.map(empleado, EmpleadoDTO.class);
        if (empleado.getDepartamento() != null) {
            dto.setNombreDepartamento(empleado.getDepartamento().getNombreDept());
        }
        return dto;
    }

}
