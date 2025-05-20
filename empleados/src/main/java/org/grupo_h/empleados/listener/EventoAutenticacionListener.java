package org.grupo_h.empleados.listener;

import org.grupo_h.empleados.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Listener que maneja eventos de autenticación exitosa y fallida.
 */
@Component
public class EventoAutenticacionListener{

    private static final Logger logger = LoggerFactory.getLogger(EventoAutenticacionListener.class);

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Maneja un evento de autenticación exitosa.
     *
     * @param event Evento de autenticación exitosa.
     */
    @EventListener
    public void handleAuthenticationSuccess(AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            System.out.println("Login exitoso para: " + username);
            usuarioService.procesarLoginExitoso(username);
        } else if (principal instanceof String) {
            String username = (String) principal;
            System.out.println("Login exitoso para: " + username);
            usuarioService.procesarLoginExitoso(username);
        }
    }

    /**
     * Maneja un evento de autenticación fallida debido a credenciales incorrectas.
     *
     * @param event Evento de autenticación fallida.
     */
    @EventListener
    public void handleAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof String) {
            String username = (String) principal;
            System.out.println("Login fallido (BadCredentials) para: " + username);
            usuarioService.procesarLoginFallido(username);
        }
    }
}
