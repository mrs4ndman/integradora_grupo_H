package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.Administrador;
import org.grupo_h.comun.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AdministradorRepository extends JpaRepository<Administrador, UUID> {
    Optional<Administrador> findByEmail(String email);

    Optional<Administrador> findByRememberMeToken(String rememberMeToken);
}
