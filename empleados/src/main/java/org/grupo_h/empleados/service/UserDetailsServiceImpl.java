package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.Usuario;
import org.grupo_h.empleados.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Implementación de UserDetailsService para cargar los detalles del usuario durante la autenticación.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Carga los detalles del usuario por su email.
     *
     * @param email Email a buscar.
     * @return UserDetails con los detalles del usuario.
     * @throws UsernameNotFoundException Si el usuario no se encuentra.
     * @throws DisabledException         Si la cuenta del usuario está deshabilitada.
     * @throws LockedException           Si la cuenta del usuario está bloqueada.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException, DisabledException, LockedException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado para el email: " + email));

        if (!usuario.isHabilitado()) {
            throw new DisabledException("La cuenta del usuario con email: "+ email + " está deshabilitada");
        }

        if (usuario.isCuentaBloqueada()) {
            throw new LockedException("La cuenta del usuario con email: "+ email + " está bloqueada");
        }

        return new User(usuario.getEmail(),
                usuario.getContrasena(),
                usuario.isHabilitado(),
                true,
                true,
                !usuario.isCuentaBloqueada(),
                Collections.emptyList());
    }
}
