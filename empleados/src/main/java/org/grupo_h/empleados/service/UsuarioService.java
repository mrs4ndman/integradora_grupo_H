package org.grupo_h.empleados.service;

import org.grupo_h.empleados.dto.UsuarioRegistroDTO;
import org.grupo_h.comun.entity.Usuario;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UsuarioService {
    Usuario registrarUsuario(UsuarioRegistroDTO usuarioDTO);

    // Métodos para manejar intentos de login
    void procesarLoginFallido(String email);

    void procesarLoginExitoso(String email);

    void desbloquearCuenta(String email);
}
