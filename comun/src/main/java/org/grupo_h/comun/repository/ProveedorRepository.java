package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProveedorRepository extends JpaRepository<Proveedor, UUID> {
    Optional<Proveedor> findByNombre(String proveedor);
}