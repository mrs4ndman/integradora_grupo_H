package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.auxiliar.TipoTarjetaCredito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TipoTarjetaCreditoRepository extends JpaRepository<TipoTarjetaCredito, UUID> {
    Optional<TipoTarjetaCredito> findByNombreTipoTarjeta(String nombreTipoTarjeta); // Añadir este método
}
