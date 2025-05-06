package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByRememberMeToken(String rememberMeToken);

    List<Usuario> findByEmailContainingIgnoreCase(String email);

    // Optional<Usuario> findAllByCuentaBloqueada(boolean estatus);
}
