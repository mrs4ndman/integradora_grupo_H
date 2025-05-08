package org.grupo_h.comun.repository;

import org.grupo_h.comun.entity.Producto;
import org.grupo_h.comun.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // Para consultas dinámicas
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, UUID>, JpaSpecificationExecutor<Producto> {

     List<Producto> findByMarca(String marca);

    Optional<Producto> findByDescripcionAndProveedor(String descripcion, Proveedor proveedor);

    // Para la importación, podrías necesitar un método para buscar por un identificador único
    // que no sea el UUID, si es que los productos vienen con un SKU o EAN.
    // Por ejemplo, si añades un campo 'sku' a tu entidad Producto:
    // Optional<Producto> findBySku(String sku);
}