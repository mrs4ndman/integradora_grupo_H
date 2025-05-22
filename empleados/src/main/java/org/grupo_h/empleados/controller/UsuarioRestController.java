package org.grupo_h.empleados.controller;

import jakarta.servlet.http.HttpSession;
import org.grupo_h.comun.repository.UsuarioRepository;
import org.grupo_h.empleados.dto.UsuarioRegistroDTO;
import org.grupo_h.comun.entity.Usuario;
import org.grupo_h.empleados.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST para gestionar las operaciones relacionadas con los usuarios.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioRestController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    /**
     * Constructor para inyectar el servicio de usuarios.
     *
     * @param usuarioService Servicio de usuarios.
     */
    @Autowired
    public UsuarioRestController(UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Registra un nuevo usuario.
     *
     * @param usuarioDTO DTO con los datos del usuario a registrar.
     * @return Respuesta con el usuario creado o un mensaje de error.
     */
    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@Validated @RequestBody UsuarioRegistroDTO usuarioDTO) {
        try {
            Usuario usuarioCreado = usuarioService.registrarUsuario(usuarioDTO);
            return new ResponseEntity<>(usuarioCreado, HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/area-personal/info")
    public ResponseEntity<?> getInfoAreaPersonal(HttpSession session) {
        String email = (String) session.getAttribute("emailAutenticado");

        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Usuario no autenticado. No se encontró 'emailAutenticado' en la sesión."));
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario con email '" + email + "' no encontrado en la base de datos."));
        }

        Usuario usuario = usuarioOpt.get();
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("email", email);
        responseData.put("totalLoginsUsuario", usuario.getSesionesTotales());

        // Obtener atributos directamente de la sesión actual
        Object contadorConexionesObj = session.getAttribute("contadorConexiones");
        responseData.put("loginsSesionActual", contadorConexionesObj instanceof Integer ? (Integer) contadorConexionesObj : 0);
        responseData.put("navegadorActual", session.getAttribute("userAgent"));

        return ResponseEntity.ok(responseData);
    }
}
