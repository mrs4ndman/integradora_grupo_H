package org.grupo_h.administracion.specs;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.JoinType; // Importar JoinType
import org.grupo_h.comun.entity.Categoria;
import org.grupo_h.comun.entity.Producto;
import org.grupo_h.comun.entity.Proveedor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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

            // Filtrar por descripción
            if (StringUtils.hasText(descripcion)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("descripcion")), "%" + descripcion.toLowerCase() + "%"));
            }

            // Filtrar por categoría
            if (categoriaId != null) {
                Join<Producto, Categoria> categoriaJoin = root.join("categorias", JoinType.INNER);
                predicates.add(criteriaBuilder.equal(categoriaJoin.get("id"), categoriaId));
            }

            // Filtrar por precio mínimo
            if (precioMin != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("precio"), precioMin));
            }

            // Filtrar por precio máximo
            if (precioMax != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("precio"), precioMax));
            }

            // Filtrar por proveedores
            if (!CollectionUtils.isEmpty(proveedorIds)) {
                Join<Producto, Proveedor> proveedorJoin = root.join("proveedor", JoinType.INNER);
                predicates.add(proveedorJoin.get("id").in(proveedorIds));
            }

            // Filtrar por si es perecedero
            if (esPerecedero != null) {
                predicates.add(criteriaBuilder.equal(root.get("esPerecedero"), esPerecedero));
            }

            if (predicates.isEmpty()) {
                return criteriaBuilder.conjunction(); // Equivale a un predicado 'true'
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
