package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.DocumentoDNI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface DocumentoDniRepository extends JpaRepository<DocumentoDNI, UUID> {
}
