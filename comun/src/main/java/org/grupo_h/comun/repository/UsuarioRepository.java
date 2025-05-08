package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    @Query("SELECT DISTINCT u FROM Usuario u LEFT JOIN FETCH u.empleado")
    List<Usuario> findAllDistinct();

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByRememberMeToken(String rememberMeToken);

    List<Usuario> findByEmailContainingIgnoreCase(String email);

    Optional<Usuario> findByEmailIgnoreCase(String username);

    // Optional<Usuario> findAllByCuentaBloqueada(boolean estatus);
}
