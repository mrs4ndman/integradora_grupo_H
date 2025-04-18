package org.grupo_h.empleados.service;

import jakarta.transaction.Transactional;
import org.grupo_h.empleados.dto.UsuarioRegistroDTO;
import org.grupo_h.comun.entity.Usuario;
import org.grupo_h.empleados.repository.UsuarioRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final int MAX_INTENTOS_FALLIDOS = 2;

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario registrarUsuario(UsuarioRegistroDTO usuarioDTO) {
        // Validar si el usuario ya existe
        if (usuarioRepository.findByNombreUsuario(usuarioDTO.getNombreUsuario()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }

        // Crear la entidad de Usuario y completar los datos
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(usuarioDTO.getNombreUsuario());
        usuario.setEmail(usuarioDTO.getEmail());
        // Cifrar la contraseña
        String encodedPassword = passwordEncoder.encode(usuarioDTO.getContrasena());
        usuario.setContrasena(encodedPassword);
        usuario.setHabilitado(true);
        usuario.setCuentaBloqueada(false);
        usuario.setIntentosFallidos(0);
        // Persistir la entidad
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional // Aseguramos que los cambios se guarden
    public void procesarLoginFallido(String nombreUsuario) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombreUsuario(nombreUsuario);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (usuario.isHabilitado() && !usuario.isCuentaBloqueada()) {
                usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);
                if (usuario.getIntentosFallidos() >= MAX_INTENTOS_FALLIDOS) {
                    usuario.setCuentaBloqueada(true);
                    // Registrar tiempo de bloqueo en caso de que sea necesario
                    // usuario.setTiempoBloqueo(java.time.LocalDateTime.now());
                    System.out.println("Cuenta bloqueada para usuario: " + nombreUsuario); // Log
                }
                usuarioRepository.save(usuario);
            }
        }
        // Si el usuario no existe, no hacemos nada (o podrías loggear el intento fallido)
    }

    @Override
    @Transactional // Aseguramos que los cambios se guarden
    public void procesarLoginExitoso(String nombreUsuario) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombreUsuario(nombreUsuario);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Solo reseteamos si había intentos fallidos previos
            if (usuario.getIntentosFallidos() > 0) {
                usuario.setIntentosFallidos(0);
                // Limpiar tiempo de bloqueo si es necesario
                // usuario.setTiempoBloqueo(null);
                usuarioRepository.save(usuario);
            }
        }
    }
}