package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.Usuario;
import org.grupo_h.comun.repository.UsuarioRepository;
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
     * Carga los detalles del usuario por su nombre de usuario.
     *
     * @param nombreUsuario Nombre de usuario a buscar.
     * @return UserDetails con los detalles del usuario.
     * @throws UsernameNotFoundException Si el usuario no se encuentra.
     * @throws DisabledException         Si la cuenta del usuario está deshabilitada.
     * @throws LockedException           Si la cuenta del usuario está bloqueada.
     */
    @Override
    public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException, DisabledException, LockedException {
        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + nombreUsuario));

        if (!usuario.isHabilitado()) {
            throw new DisabledException("La cuenta del usuario está deshabilitada: " + nombreUsuario);
        }

        if (usuario.isCuentaBloqueada()) {
            throw new LockedException("La cuenta del usuario está bloqueada: " + nombreUsuario);
        }

        return new User(usuario.getNombreUsuario(),
                usuario.getContrasena(),
                usuario.isHabilitado(),
                true,
                true,
                !usuario.isCuentaBloqueada(),
                Collections.emptyList());
    }
}
