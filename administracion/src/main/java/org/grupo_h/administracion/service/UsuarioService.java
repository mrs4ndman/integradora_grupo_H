package org.grupo_h.administracion.service;

import org.grupo_h.comun.entity.Usuario;
import org.grupo_h.comun.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    private UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    public Usuario buscarPorId(UUID id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public List<Usuario> buscarPorFiltro(String filtro) {
        return usuarioRepository.findByEmailContainingIgnoreCase(filtro);
    }

    // Bloquear usuario desde Administración
    public void bloquearUsuarioAdmin(UUID id, String motivoBloqueo, int duracionBloqueoMinutosAdmin) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario != null) {
            usuario.setCuentaBloqueada(true);
            usuario.setTiempoHastaDesbloqueo(LocalDateTime.now().plusMinutes(duracionBloqueoMinutosAdmin));
            usuario.setMotivoBloqueo(motivoBloqueo);
            usuarioRepository.save(usuario);
        }
    }

    public void desbloquearUsuario(UUID id) {
        Usuario usuario = this.buscarPorId(id);
        if (usuario != null) {
            usuario.setCuentaBloqueada(false);
            usuario.setMotivoBloqueo("");
            usuario.setTiempoHastaDesbloqueo(null);
            usuarioRepository.save(usuario);
        }
    }

    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    public void actualizarUsuario(Usuario usuario) {
        usuarioRepository.save(usuario);
    }
}
