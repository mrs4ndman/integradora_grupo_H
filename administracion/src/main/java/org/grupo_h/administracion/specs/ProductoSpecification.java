package org.grupo_h.administracion.specs;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.grupo_h.comun.entity.Categoria;
import org.grupo_h.comun.entity.Producto;
import org.grupo_h.comun.entity.Proveedor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils; // Para StringUtils.hasText

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductoSpecification {

    public static Specification<Producto> conCriterios(
            String descripcion,
            UUID categoriaId,
            Double precioMin,
            Double precioMax,
            List<UUID> proveedorIds,
            Boolean esPerecedero
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(descripcion)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("descripcion")), "%" + descripcion.toLowerCase() + "%"));
            }

            if (categoriaId != null) {
                Join<Producto, Categoria> categoriaJoin = root.join("categoria");
                predicates.add(criteriaBuilder.equal(categoriaJoin.get("id"), categoriaId));
            }

            if (precioMin != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("precio"), precioMin));
            }

            if (precioMax != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("precio"), precioMax));
            }

            if (!CollectionUtils.isEmpty(proveedorIds)) {
                Join<Producto, Proveedor> proveedorJoin = root.join("proveedor");
                if (proveedorIds.size() == 1) {
                    predicates.add(criteriaBuilder.equal(proveedorJoin.get("id"), proveedorIds.get(0)));
                } else {
                    predicates.add(proveedorJoin.get("id").in(proveedorIds));
                }
            }

            if (esPerecedero  != null) {
                predicates.add(criteriaBuilder.equal(root.get("esPerecedero"), esPerecedero));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
