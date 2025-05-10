package org.grupo_h.administracion.service;

import org.grupo_h.comun.entity.Usuario;
import org.grupo_h.comun.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Servicio que gestiona las operaciones relacionadas con usuarios
 * desde el módulo de administración.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Constructor del servicio de usuarios
     *
     * @param usuarioRepository repositorio de usuarios
     */
    private UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Busca un usuario por su dirección de email
     *
     * @param email dirección de correo electrónico
     * @return usuario encontrado o null si no existe
     */
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    /**
     * Busca un usuario por su identificador único
     *
     * @param id identificador UUID del usuario
     * @return usuario encontrado o null si no existe
     */
    public Usuario buscarPorId(UUID id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    /**
     * Busca usuarios cuyo email contiene el filtro especificado
     *
     * @param filtro texto para filtrar emails
     * @return lista de usuarios que coinciden con el filtro
     */
    public List<Usuario> buscarPorFiltro(String filtro) {
        return usuarioRepository.findByEmailContainingIgnoreCase(filtro);
    }

    /**
     * Bloquea un usuario desde el panel de administración
     *
     * @param id                          Identificador en formato {@link UUID} del usuario
     * @param motivoBloqueo               Razón por la que se bloquea la cuenta
     * @param duracionBloqueoMinutosAdmin Duración del bloqueo en minutos
     */
    public void bloquearUsuarioAdmin(UUID id, String motivoBloqueo, int duracionBloqueoMinutosAdmin) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario != null) {
            usuario.setCuentaBloqueada(true);
            usuario.setTiempoHastaDesbloqueo(LocalDateTime.now().plusMinutes(duracionBloqueoMinutosAdmin));
            usuario.setMotivoBloqueo(motivoBloqueo);
            usuarioRepository.save(usuario);
        }
    }

    /**
     * Desbloquea un usuario previamente bloqueado
     *
     * @param id {@link UUID} identificador del usuario a desbloquear
     */
    public void desbloquearUsuario(UUID id) {
        Usuario usuario = this.buscarPorId(id);
        if (usuario != null) {
            usuario.setCuentaBloqueada(false);
            usuario.setMotivoBloqueo("");
            usuario.setTiempoHastaDesbloqueo(null);
            usuarioRepository.save(usuario);
        }
    }

    /**
     * Obtiene todos los usuarios registrados en el sistema
     *
     * @return lista completa de todos los objetos de {@link Usuario} en la
     * base de datos
     */
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAllDistinct();
    }

    /**
     * Actualiza la información de un usuario existente
     *
     * @param usuario objeto {@link Usuario} con los datos actualizados
     */
    public void actualizarUsuario(Usuario usuario) {
        usuarioRepository.save(usuario);
    }
}
