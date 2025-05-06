package org.grupo_h.administracion.service;

import org.grupo_h.administracion.dto.AdministradorRegistroDTO;
import org.grupo_h.comun.entity.Administrador;
import org.springframework.stereotype.Service;

@Service
public interface AdministradorService {
    // Métodos para manejar intentos de login
    void procesarLoginFallido(String email);

    void procesarLoginExitoso(String email);

    // Método para desbloquear una cuenta bloqueada
    void desbloquearCuenta(String email);
}
