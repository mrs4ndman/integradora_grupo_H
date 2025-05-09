package org.grupo_h.empleados.service;

import jakarta.persistence.EntityNotFoundException;
import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.comun.entity.Departamento;
import org.grupo_h.comun.entity.auxiliar.*;
import org.grupo_h.comun.repository.*;
import org.grupo_h.comun.entity.Etiqueta;
import org.grupo_h.comun.entity.Usuario;
import org.grupo_h.comun.entity.auxiliar.CuentaCorriente;
import org.grupo_h.comun.entity.auxiliar.Direccion;
import org.grupo_h.comun.entity.auxiliar.TarjetaCredito;
import org.grupo_h.empleados.dto.*;
import org.grupo_h.comun.repository.EmpleadoRepository;
import org.modelmapper.ModelMapper;
import org.grupo_h.comun.repository.GeneroRepository;
import org.grupo_h.comun.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.yaml.snakeyaml.events.Event;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmpleadoServiceImpl implements EmpleadoService {

    private final EmpleadoRepository empleadosRepository;
    public final EntidadBancariaRepository entidadBancariaRepository;
    private final UsuarioRepository usuarioRepository;
    private final GeneroRepository generoRepository;
    private final EtiquetaService etiquetaService;
    private final TipoTarjetaCreditoRepository tipoTarjetaCreditoRepository;
    private final DepartamentoRepository departamentoRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public EmpleadoServiceImpl(EmpleadoRepository empleadosRepository,
                               UsuarioRepository usuarioRepository,
                               GeneroRepository generoRepository,
                               EtiquetaService etiquetaService,
                               EntidadBancariaRepository entidadBancariaRepository,
                               TipoTarjetaCreditoRepository tipoTarjetaCreditoRepository,
                               DepartamentoRepository departamentoRepository,
                               ModelMapper modelMapper) {
        this.empleadosRepository = empleadosRepository;
        this.usuarioRepository = usuarioRepository;
        this.generoRepository = generoRepository;
        this.etiquetaService = etiquetaService;
        this.modelMapper = modelMapper;
        this.entidadBancariaRepository = entidadBancariaRepository;
        this.tipoTarjetaCreditoRepository = tipoTarjetaCreditoRepository;
        this.departamentoRepository = departamentoRepository;
    }

    @Override
    @Transactional
    public Empleado registrarEmpleado(EmpleadoRegistroDTO empleadoDTO, UUID IdUsuario) {
        Empleado empleado = modelMapper.map(empleadoDTO, Empleado.class);
        Optional<Usuario> usuario = usuarioRepository.findById(IdUsuario);

        if (usuario.isPresent()) {
            empleado.setUsuario(usuario.get());
        }

        if (empleado.getTarjetas() != null && empleado.getTarjetas().getTipoTarjetaCredito() != null) {
            String nombreTipoTarjeta = empleado.getTarjetas().getTipoTarjetaCredito().getNombreTipoTarjeta();
            if (nombreTipoTarjeta != null && !nombreTipoTarjeta.isEmpty()) {
                Optional<TipoTarjetaCredito> tipoExistenteOpt = tipoTarjetaCreditoRepository.findByNombreTipoTarjeta(nombreTipoTarjeta);

                if (tipoExistenteOpt.isPresent()) {
                    empleado.getTarjetas().setTipoTarjetaCredito(tipoExistenteOpt.get());
                } else {
                    throw new RuntimeException("TipoTarjetaCredito no encontrado con nombre: " + nombreTipoTarjeta + " y no se permite la creación automática.");
                }
            } else if (empleado.getTarjetas().getTipoTarjetaCredito() != null) { // Si el objeto existe pero sin nombre
                    throw new RuntimeException("Se proporcionó una tarjeta de crédito pero su tipo es nulo o vacío.");
            }
        }

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

    @Override
    public Optional<Empleado> obtenerEmpleadoPorId(UUID id) {
        return empleadosRepository.findById(id);
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

    @Override
    @Transactional(readOnly = true)
    public List<Empleado> getSubordinadosDelJefeAutenticado() throws AccessDeniedException {
        Empleado jefe = getEmpleadoAutenticado();
        return empleadosRepository.findByJefe(jefe);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Empleado> getSubordinadosByJefeId(UUID jefeId) {
        return empleadosRepository.findByJefeId(jefeId);
    }


    @Override
    @Transactional
    public void asignarEtiquetaASubordinado(UUID subordinadoId, UUID etiquetaId) throws AccessDeniedException {
        Empleado jefe = getEmpleadoAutenticado();
        Empleado subordinado = empleadosRepository.findById(subordinadoId)
                .orElseThrow(() -> new EntityNotFoundException("Empleado subordinado no encontrado con ID: " + subordinadoId));

        // Verificar relación Jefe-Subordinado
        if (!esJefeDirecto(jefe, subordinado)) {
            throw new AccessDeniedException("Acción no permitida: No eres el jefe directo de este empleado.");
        }

        Etiqueta etiqueta = etiquetaService.findById(etiquetaId)
                .orElseThrow(() -> new EntityNotFoundException("Etiqueta no encontrada con ID: " + etiquetaId));

        subordinado.addEtiqueta(etiqueta); // Usa el método helper en Empleado
        empleadosRepository.save(subordinado); // Guarda los cambios
    }

    @Override
    @Transactional
    public void eliminarEtiquetaDeSubordinado(UUID subordinadoId, UUID etiquetaId) throws AccessDeniedException {
        Empleado jefe = getEmpleadoAutenticado();
        Empleado subordinado = empleadosRepository.findById(subordinadoId)
                .orElseThrow(() -> new EntityNotFoundException("Empleado subordinado no encontrado con ID: " + subordinadoId));

        // Verificar relación Jefe-Subordinado
        if (!esJefeDirecto(jefe, subordinado)) {
            throw new AccessDeniedException("Acción no permitida: No eres el jefe directo de este empleado.");
        }

        Etiqueta etiqueta = etiquetaService.findById(etiquetaId)
                .orElseThrow(() -> new EntityNotFoundException("Etiqueta no encontrada con ID: " + etiquetaId));

        // Verificar si el empleado realmente tiene la etiqueta antes de intentar quitarla
        if (!subordinado.getEtiquetas().contains(etiqueta)) {
            // Puedes lanzar una excepción o simplemente no hacer nada
            // throw new EntityNotFoundException("El empleado no tiene asignada la etiqueta con ID: " + etiquetaId);
            return; // No hacer nada si no la tiene
        }


        subordinado.removeEtiqueta(etiqueta); // Usa el método helper en Empleado
        empleadosRepository.save(subordinado);
    }

    @Override
    @Transactional
    public void asignarEtiquetasMasivo(List<UUID> subordinadoIds, List<UUID> etiquetaIds) throws AccessDeniedException {
        if (subordinadoIds == null || subordinadoIds.isEmpty() || etiquetaIds == null || etiquetaIds.isEmpty()) {
            throw new IllegalArgumentException("Las listas de IDs de empleados y etiquetas no pueden estar vacías.");
        }

        Empleado jefe = getEmpleadoAutenticado();
        List<Etiqueta> etiquetas = etiquetaService.findByIds(etiquetaIds);

        if(etiquetas.size() != etiquetaIds.size()){
            // Lógica para manejar el caso de que algún ID de etiqueta no exista
            throw new EntityNotFoundException("Una o más etiquetas no fueron encontradas.");
        }


        List<Empleado> subordinados = empleadosRepository.findAllById(subordinadoIds);

        if(subordinados.size() != subordinadoIds.size()){
            // Lógica para manejar el caso de que algún ID de subordinado no exista
            throw new EntityNotFoundException("Uno o más empleados subordinados no fueron encontrados.");
        }

        // Verificar que TODOS son subordinados del jefe autenticado
        for (Empleado sub : subordinados) {
            if (!esJefeDirecto(jefe, sub)) {
                throw new AccessDeniedException(String.format(
                        "Acción no permitida: No eres el jefe directo del empleado con ID %s.", sub.getId()));
            }
            // Asignar todas las etiquetas encontradas a este subordinado
            etiquetas.forEach(sub::addEtiqueta);
        }

        // Guardar todos los empleados modificados
        empleadosRepository.saveAll(subordinados);
    }


    @Override
    @Transactional(readOnly = true) // Método de solo lectura
    public boolean esJefeDirecto(UUID subordinadoId) throws AccessDeniedException {
        Empleado jefeAutenticado = getEmpleadoAutenticado();
        Empleado posibleSubordinado = empleadosRepository.findById(subordinadoId)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con ID: " + subordinadoId));
        return esJefeDirecto(jefeAutenticado, posibleSubordinado);
    }

    private Empleado getEmpleadoAutenticado() throws AccessDeniedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AccessDeniedException("Usuario no autenticado.");
        }

        // Asume que el 'name' de la autenticación es el username (email) del Usuario
        String username = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));


        // Busca el empleado asociado al usuario
        // Asumiendo que tienes findByUsuarioId o findByUsuario en EmpleadoRepository
        return (Empleado) empleadosRepository.findByUsuarioId(usuario.getId()) // O usa findByUsuario(usuario)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado para el usuario: " + username));
    }

    private boolean esJefeDirecto(Empleado posibleJefe, Empleado posibleSubordinado) {
        return posibleSubordinado.getJefe() != null &&
                posibleJefe.getId().equals(posibleSubordinado.getJefe().getId());
    }
    public List<EmpleadoRegistroDTO> findAllEmpleados() {
        // Implementación de ejemplo
        return empleadosRepository.findAll().stream()
                .map(emp -> modelMapper.map(emp, EmpleadoRegistroDTO.class))
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public Optional<EmpleadoDetalleDTO> findEmpleadoDetalleById(UUID id) {
        return empleadosRepository.findById(id)
                .map(empleado -> modelMapper.map(empleado, EmpleadoDetalleDTO.class));
    }
}