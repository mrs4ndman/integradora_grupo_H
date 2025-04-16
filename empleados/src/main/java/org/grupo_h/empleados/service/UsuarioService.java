package org.grupo_h.empleados.service;

import org.grupo_h.empleados.dto.UsuarioRegistroDTO;
import org.grupo_h.comun.entity.Usuario;

public interface UsuarioService {
    Usuario registrarUsuario(UsuarioRegistroDTO usuarioDTO);
}
