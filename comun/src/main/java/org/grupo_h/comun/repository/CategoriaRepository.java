package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {
    Optional<Categoria> findByNombre(String nombreCategoriaPrincipal);
}