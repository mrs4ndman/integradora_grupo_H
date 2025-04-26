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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.Locale;

/**
 * Implementación de UserDetailsService para cargar los detalles del usuario durante la autenticación.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

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
            throw new DisabledException("La cuenta del usuario con email: " + email + " está deshabilitada");
        }

        // Comprobar si la cuenta está bloqueada
        if (usuario.isCuentaBloqueada()) {
            LocalDateTime horaDesbloqueo = usuario.getTiempoHastaDesbloqueo(); // Obtiene la hora de desbloqueo guardada

            if (horaDesbloqueo != null && LocalDateTime.now().isBefore(horaDesbloqueo)) {
                // Todavía está bloqueada: Lanza la excepción con la hora de desbloqueo
                DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                        .withLocale(new Locale("es", "ES")); // Locale para formato español
                String unlockTimeString = horaDesbloqueo.format(formatter);
                throw new LockedException("Su cuenta está bloqueada temporalmente. Podrá intentar de nuevo después de las " + unlockTimeString);

            } else {
                // El tiempo de bloqueo ha pasado o no había tiempo definido (caso raro): Desbloquearla
                usuarioService.desbloquearCuenta(email);
                // Actualizar la instancia local del usuario para reflejar el desbloqueo
                usuario.setCuentaBloqueada(false);
                usuario.setIntentosFallidos(0);
                usuario.setTiempoHastaDesbloqueo(null);
            }
        }

        // Si llega aquí, la cuenta no está bloqueada (o acaba de ser desbloqueada)
        return new User(usuario.getEmail(),
                usuario.getContrasena(),
                usuario.isHabilitado(),
                true,
                true,
                !usuario.isCuentaBloqueada(), // Debería ser true ahora
                Collections.emptyList());
    }
}
