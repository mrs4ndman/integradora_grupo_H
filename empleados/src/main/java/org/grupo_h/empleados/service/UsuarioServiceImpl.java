package org.grupo_h.empleados.service;

import jakarta.transaction.Transactional;
import org.grupo_h.empleados.dto.UsuarioRegistroDTO;
import org.grupo_h.comun.entity.Usuario;
import org.grupo_h.comun.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Implementación del servicio para gestionar operaciones relacionadas con los usuarios.
 */
@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ParametrosService parametrosService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    /**
     * Constructor para inyectar las dependencias necesarias.
     *
     * @param usuarioRepository Repositorio de usuarios.
     * @param passwordEncoder   Codificador de contraseñas.
     */
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, ParametrosService parametrosService, BCryptPasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.usuarioRepository = usuarioRepository;
        this.parametrosService = parametrosService;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param usuarioRegistroDTO DTO con los datos del usuario a registrar.
     * @return El usuario registrado.
     * @throws RuntimeException Si el nombre de usuario ya existe.
     */
    @Override
    public Usuario registrarUsuario(UsuarioRegistroDTO usuarioRegistroDTO) {
        Usuario usuarioDelDTO = modelMapper.map(usuarioRegistroDTO, Usuario.class);
        if (usuarioRepository.findByEmail(usuarioRegistroDTO.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya existe");
        }

//        usuario.setNombreUsuario(usuarioDTO.getNombreUsuario());
        Usuario usuario = new Usuario();
        usuario.setEmail(usuarioRegistroDTO.getEmail());
        String encodedPassword = passwordEncoder.encode(usuarioRegistroDTO.getContrasena());
        usuario.setContrasena(encodedPassword);
        usuario.setHabilitado(true);
        usuario.setCuentaBloqueada(false);
        usuario.setIntentosFallidos(0);

        return usuarioRepository.save(usuario);
    }

    /**
     * Procesa un intento de inicio de sesión fallido.
     *
     * @param email Email que intentó iniciar sesión.
     */
    @Override
    @Transactional // Usar @Transactional de Spring o Jakarta según configuración
    public void procesarLoginFallido(String email) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (usuario.isHabilitado() && !usuario.isCuentaBloqueada()) {
                usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);

                // Obtener valores desde el servicio
                int maxIntentos = parametrosService.getMaxIntentosFallidos();
                int duracionBloqueo = parametrosService.getDuracionBloqueoMinutos();

                if (usuario.getIntentosFallidos() >= maxIntentos) { // Usar valor del servicio
                    usuario.setCuentaBloqueada(true);
                    LocalDateTime horaDesbloqueo = LocalDateTime.now().plus(duracionBloqueo, ChronoUnit.MINUTES); // Usar valor del servicio
                    usuario.setTiempoHastaDesbloqueo(horaDesbloqueo);
                    usuario.setMotivoBloqueo("Bloqueado por exceder el número máximo de intentos fallidos");
                    System.out.println("Cuenta bloqueada para usuario con email: " + email + ". Se desbloqueará a las: " + horaDesbloqueo);
                }
                usuarioRepository.save(usuario);
            }
        }
    }

    /**
     * Procesa un inicio de sesión exitoso.
     *
     * @param email Email que inició sesión.
     */
    @Override
    @Transactional
    public void procesarLoginExitoso(String email) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (usuario.getIntentosFallidos() > 0 || usuario.isCuentaBloqueada()) {
                usuario.setIntentosFallidos(0);
                usuario.setTiempoHastaDesbloqueo(null);
                usuario.setCuentaBloqueada(false);
                usuarioRepository.save(usuario);
            }
        }
    }

    /**
     * Desbloquea una cuenta de usuario reseteando los intentos fallidos y el tiempo de bloqueo.
     * @param email Email del usuario a desbloquear.
     */
    @Override
    @Transactional
    public void desbloquearCuenta(String email) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if(usuario.isCuentaBloqueada()){ // Solo si está bloqueada
                usuario.setCuentaBloqueada(false);
                usuario.setIntentosFallidos(0);
                usuario.setTiempoHastaDesbloqueo(null); // Resetea el tiempo de bloqueo
                usuario.setMotivoBloqueo("");
                usuarioRepository.save(usuario);
            }
        }
    }

    @Override
    public boolean actualizarPassword(String email, String nuevaPassword) {
        if (email == null || email.isEmpty() || nuevaPassword == null || nuevaPassword.isEmpty()) {
            return false;
        }
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            try {
                usuario.setContrasena(passwordEncoder.encode(nuevaPassword));
                usuarioRepository.save(usuario);
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public Optional<Usuario> findByEmail(String email){
        return usuarioRepository.findByEmail(email);
    }
}
