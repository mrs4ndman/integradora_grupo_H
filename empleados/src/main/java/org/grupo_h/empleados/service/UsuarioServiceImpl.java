package org.grupo_h.empleados.service;

import jakarta.transaction.Transactional;
import org.grupo_h.empleados.dto.UsuarioRegistroDTO;
import org.grupo_h.comun.entity.Usuario;
import org.grupo_h.empleados.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementación del servicio para gestionar operaciones relacionadas con los usuarios.
 */
@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final int MAX_INTENTOS_FALLIDOS = 3;

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Constructor para inyectar las dependencias necesarias.
     *
     * @param usuarioRepository Repositorio de usuarios.
     * @param passwordEncoder   Codificador de contraseñas.
     */
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param usuarioDTO DTO con los datos del usuario a registrar.
     * @return El usuario registrado.
     * @throws RuntimeException Si el nombre de usuario ya existe.
     */
    @Override
    public Usuario registrarUsuario(UsuarioRegistroDTO usuarioDTO) {
        if (usuarioRepository.findByNombreUsuario(usuarioDTO.getNombreUsuario()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(usuarioDTO.getNombreUsuario());
        usuario.setEmail(usuarioDTO.getEmail());
        String encodedPassword = passwordEncoder.encode(usuarioDTO.getContrasena());
        usuario.setContrasena(encodedPassword);
        usuario.setHabilitado(true);
        usuario.setCuentaBloqueada(false);
        usuario.setIntentosFallidos(0);

        return usuarioRepository.save(usuario);
    }

    /**
     * Procesa un intento de inicio de sesión fallido.
     *
     * @param nombreUsuario Nombre de usuario que intentó iniciar sesión.
     */
    @Override
    @Transactional
    public void procesarLoginFallido(String nombreUsuario) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombreUsuario(nombreUsuario);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (usuario.isHabilitado() && !usuario.isCuentaBloqueada()) {
                usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);
                if (usuario.getIntentosFallidos() >= MAX_INTENTOS_FALLIDOS) {
                    usuario.setCuentaBloqueada(true);
                    System.out.println("Cuenta bloqueada para usuario: " + nombreUsuario);
                }
                usuarioRepository.save(usuario);
            }
        }
    }

    /**
     * Procesa un inicio de sesión exitoso.
     *
     * @param nombreUsuario Nombre de usuario que inició sesión.
     */
    @Override
    @Transactional
    public void procesarLoginExitoso(String nombreUsuario) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombreUsuario(nombreUsuario);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (usuario.getIntentosFallidos() > 0) {
                usuario.setIntentosFallidos(0);
                usuarioRepository.save(usuario);
            }
        }
    }
}
