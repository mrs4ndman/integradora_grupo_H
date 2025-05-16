package org.grupo_h.empleados.service;

import jakarta.mail.Multipart;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.comun.entity.Departamento;
import org.grupo_h.comun.entity.auxiliar.*;
import org.grupo_h.comun.repository.*;
import org.grupo_h.comun.entity.Etiqueta;
import org.grupo_h.comun.entity.Usuario;
import org.grupo_h.empleados.dto.*;
import org.grupo_h.comun.repository.EmpleadoRepository;
import org.grupo_h.comun.exceptions.EntidadDuplicadaEnSesionException;
import org.hibernate.NonUniqueObjectException;
import org.modelmapper.ModelMapper;
import org.grupo_h.comun.repository.GeneroRepository;
import org.grupo_h.comun.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.yaml.snakeyaml.events.Event;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class EmpleadoServiceImpl implements EmpleadoService {

    private final EmpleadoRepository empleadosRepository;
    public final EntidadBancariaRepository entidadBancariaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EtiquetaRepository etiquetaRepository;
    private final GeneroRepository generoRepository;
    private final EtiquetaService etiquetaService;
    private final TipoTarjetaCreditoRepository tipoTarjetaCreditoRepository;
    private final DepartamentoRepository departamentoRepository;
    private final CuentaCorrienteRepository cuentaCorrienteRepository;
    private final ModelMapper modelMapper;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public EmpleadoServiceImpl(EmpleadoRepository empleadosRepository,
                               UsuarioRepository usuarioRepository,
                               GeneroRepository generoRepository,
                               EtiquetaService etiquetaService,
                               EntidadBancariaRepository entidadBancariaRepository,
                               EtiquetaRepository etiquetaRepository,
                               TipoTarjetaCreditoRepository tipoTarjetaCreditoRepository,
                               DepartamentoRepository departamentoRepository,
                               CuentaCorrienteRepository cuentaCorrienteRepository,
                               ModelMapper modelMapper) {
        this.empleadosRepository = empleadosRepository;
        this.usuarioRepository = usuarioRepository;
        this.generoRepository = generoRepository;
        this.etiquetaService = etiquetaService;
        this.etiquetaRepository = etiquetaRepository;
        this.cuentaCorrienteRepository = cuentaCorrienteRepository;
        this.modelMapper = modelMapper;
        this.entidadBancariaRepository = entidadBancariaRepository;
        this.tipoTarjetaCreditoRepository = tipoTarjetaCreditoRepository;
        this.departamentoRepository = departamentoRepository;
    }

    @Override
    @Transactional
    public Empleado registrarEmpleado(EmpleadoRegistroDTO empleadoDTO, UUID IdUsuario, byte[] foto) {
        Empleado empleado = modelMapper.map(empleadoDTO, Empleado.class);
        Optional<Usuario> usuario = usuarioRepository.findById(IdUsuario);

        if (usuario.isPresent()) {
            empleado.setUsuario(usuario.get());
        }

        System.out.println("imagen=" + empleadoDTO.getFotografiaDTO());

        // Convertir MultipartFile a byte[] y asignar
        System.out.println("size=" + empleadoDTO.getFotografiaDTO().getSize());
        empleado.setFotografia(foto);
        log.info("La fotografia del empleado se procesó correctamente");

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

        // Obtener el DTO de cuenta corriente
        CuentaCorrienteDTO cuentaCorrienteDTO = empleadoDTO.getCuentaCorrienteDTO();

        if (cuentaCorrienteDTO != null) {
            CuentaCorriente cuentaCorriente = modelMapper.map(cuentaCorrienteDTO, CuentaCorriente.class);

            // Manejo de la Entidad Bancaria
            EntidadBancariaDTO entidadDTO = cuentaCorrienteDTO.getEntidadBancaria();
            if (entidadDTO != null && entidadDTO.getNombreEntidadDTO() != null) {
                Optional<EntidadBancaria> entidadOpt = entidadBancariaRepository.findByNombreEntidad(entidadDTO.getNombreEntidadDTO());

                EntidadBancaria entidadBancaria = entidadOpt.orElseGet(() -> {
                    EntidadBancaria nuevaEntidad = new EntidadBancaria();
                    nuevaEntidad.setNombreEntidad(entidadDTO.getNombreEntidadDTO());
                    return entidadBancariaRepository.save(nuevaEntidad);
                });

                cuentaCorriente.setEntidadBancaria(entidadBancaria);
            } else {
                throw new RuntimeException("Entidad bancaria no válida o no proporcionada.");
            }

            // Persistir cuenta corriente
            cuentaCorrienteRepository.save(cuentaCorriente);


            // Asignar la cuenta ya persistida al empleado
            empleado.setCuentaCorriente(cuentaCorriente);
        }


        try {
            // Persistir el empleado
            return empleadosRepository.save(empleado);
        } catch (NonUniqueObjectException e) {
            // Si el usuario intenta registrarse de nuevo como empleado
            throw new EntidadDuplicadaEnSesionException("No puedes guardar más datos, ya existe una instancia con el mismo identificador en la sesión actual");
        }
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
    public void asignarEtiquetaAUltiplesSubordinados(UUID jefeId, List<UUID> subordinadoIds, UUID etiquetaId) throws AccessDeniedException {
        Etiqueta etiqueta = etiquetaRepository.findById(etiquetaId)
                .orElseThrow(() -> new EntityNotFoundException("Etiqueta no encontrada con ID: " + etiquetaId));

        for (UUID subordinadoId : subordinadoIds) {
            if (!esJefeDirecto(jefeId, subordinadoId)) {
                throw new AccessDeniedException("El empleado con ID " + jefeId + " no es jefe directo del empleado con ID " + subordinadoId);
            }
            Empleado subordinado = empleadosRepository.findById(subordinadoId)
                    .orElseThrow(() -> new EntityNotFoundException("Subordinado no encontrado con ID: " + subordinadoId));

            if (subordinado.getEtiquetas().stream().noneMatch(et -> et.getId().equals(etiquetaId))) {
                subordinado.getEtiquetas().add(etiqueta);
                empleadosRepository.save(subordinado);
            }
        }
    }


    @Override
    @Transactional
    public void actualizarEtiquetasSubordinado(UUID jefeId, UUID subordinadoId, List<UUID> etiquetaIdsAMantener) throws AccessDeniedException {

        if (!esJefeDirecto(jefeId, subordinadoId)) {
            throw new AccessDeniedException("El empleado con ID " + jefeId + " no es jefe directo del empleado con ID " + subordinadoId);
        }

        Empleado subordinado = empleadosRepository.findById(subordinadoId)
                .orElseThrow(() -> {
                    return new EntityNotFoundException("Subordinado no encontrado con ID: " + subordinadoId);
                });

        // Obtener la colección gestionada por Hibernate
        Set<Etiqueta> managedEtiquetas = subordinado.getEtiquetas();
        if (managedEtiquetas == null) {
            managedEtiquetas = new HashSet<>();
            subordinado.setEtiquetas(managedEtiquetas);
        }

        Set<Etiqueta> etiquetasNuevasParaAsignar;
        if (etiquetaIdsAMantener == null || etiquetaIdsAMantener.isEmpty()) {
            etiquetasNuevasParaAsignar = new HashSet<>();
        } else {
            etiquetasNuevasParaAsignar = etiquetaIdsAMantener.stream()
                    .map(idEtiqueta -> etiquetaRepository.findById(idEtiqueta)
                            .orElseThrow(() -> {
                                return new EntityNotFoundException("Etiqueta no encontrada con ID: " + idEtiqueta);
                            }))
                    .collect(Collectors.toSet());
        }

        boolean cambiado = false;
        if (managedEtiquetas.retainAll(etiquetasNuevasParaAsignar)) {
            cambiado = true;
        }
        for (Etiqueta etNueva : etiquetasNuevasParaAsignar) {
            if (managedEtiquetas.add(etNueva)) {
                cambiado = true;
            }
        }

        if (cambiado) {
            empleadosRepository.save(subordinado);
        }
    }

    @Override
    @Transactional
    public void asignarEtiquetaASubordinado(UUID jefeId, UUID subordinadoId, UUID etiquetaId) throws AccessDeniedException {
        if (!esJefeDirecto(jefeId, subordinadoId)) {
            throw new AccessDeniedException("El empleado con ID " + jefeId + " no es jefe directo del empleado con ID " + subordinadoId);
        }
        Empleado empleado = empleadosRepository.findById(subordinadoId)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con ID: " + subordinadoId));
        Etiqueta etiqueta = etiquetaRepository.findById(etiquetaId)
                .orElseThrow(() -> new EntityNotFoundException("Etiqueta no encontrada con ID: " + etiquetaId));

        if (empleado.getEtiquetas().stream().noneMatch(et -> et.getId().equals(etiquetaId))) {
            empleado.getEtiquetas().add(etiqueta);
            empleadosRepository.save(empleado);
        }
    }


    @Transactional
    @Override
    public void eliminarEtiquetaDeSubordinado(UUID subordinadoId, UUID etiquetaId) throws AccessDeniedException {
        Empleado jefeAutenticado = getJefeAutenticado();
        if (!esJefeDirecto(jefeAutenticado.getId(), subordinadoId)) {
            throw new AccessDeniedException("No tiene permiso para modificar etiquetas de este empleado.");
        }
        Empleado empleado = empleadosRepository.findById(subordinadoId)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con ID: " + subordinadoId));
        Etiqueta etiqueta = etiquetaRepository.findById(etiquetaId)
                .orElseThrow(() -> new EntityNotFoundException("Etiqueta no encontrada con ID: " + etiquetaId));

        if (empleado.getEtiquetas().removeIf(et -> et.getId().equals(etiquetaId))) {
            empleadosRepository.save(empleado);
        }
    }

    // Método para el flujo de etiquetado masivo unificado
    @Override
    @Transactional
    public void asignarEtiquetasMasivo(UUID jefeId, List<UUID> empleadoIds, List<UUID> etiquetaIds) throws AccessDeniedException {
        if (empleadoIds == null || empleadoIds.isEmpty() || etiquetaIds == null || etiquetaIds.isEmpty()) {
            throw new IllegalArgumentException("Se requieren IDs de empleados y etiquetas.");
        }

        List<Etiqueta> etiquetasAAsignar = etiquetaRepository.findAllById(etiquetaIds);
        if (etiquetasAAsignar.size() != etiquetaIds.size()) {
            throw new EntityNotFoundException("Una o más etiquetas no fueron encontradas.");
        }

        for (UUID empleadoId : empleadoIds) {
            if (!esJefeDirecto(jefeId, empleadoId)) {
                throw new AccessDeniedException("No tiene permiso para etiquetar al empleado con ID: " + empleadoId);
            }
            Empleado empleado = empleadosRepository.findById(empleadoId)
                    .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con ID: " + empleadoId));

            boolean changed = false;
            for (Etiqueta etiqueta : etiquetasAAsignar) {
                if (empleado.getEtiquetas().stream().noneMatch(et -> et.getId().equals(etiqueta.getId()))) {
                    empleado.getEtiquetas().add(etiqueta);
                    changed = true;
                }
            }
            if (changed) {
                empleadosRepository.save(empleado);
            }
        }
    }


    @Override
    @Transactional(readOnly = true)
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

        String username = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return (Empleado) empleadosRepository.findByUsuarioId(usuario.getId())
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

    private Empleado getJefeAutenticado() throws AccessDeniedException {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(false);
        if (session == null || session.getAttribute("emailAutenticado") == null) {
            throw new AccessDeniedException("Usuario no autenticado.");
        }
        String emailJefe = (String) session.getAttribute("emailAutenticado");
        Usuario usuarioJefe = usuarioRepository.findByEmail(emailJefe)
                .orElseThrow(() -> new AccessDeniedException("Usuario jefe no encontrado."));
        return (Empleado) empleadosRepository.findByUsuarioId(usuarioJefe.getId())
                .orElseThrow(() -> new AccessDeniedException("Perfil de empleado jefe no encontrado."));
    }

    @Transactional(readOnly = true)
    @Override
    public boolean esJefeDirecto(UUID jefeId, UUID subordinadoId) {
        Optional<Empleado> subordinadoOpt = empleadosRepository.findById(subordinadoId);
        return subordinadoOpt.map(subordinado -> subordinado.getJefe() != null && subordinado.getJefe().getId().equals(jefeId)).orElse(false);
    }
}