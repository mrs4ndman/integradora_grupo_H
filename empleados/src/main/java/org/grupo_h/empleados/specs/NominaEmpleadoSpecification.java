package org.grupo_h.empleados.specs;

import org.grupo_h.comun.entity.Nomina;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NominaEmpleadoSpecification {

    public static Specification<Nomina> conFiltros(
            UUID empleadoId, // ID del empleado autenticado
            LocalDate fechaDesde,
            LocalDate fechaHasta) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro OBLIGATORIO por empleadoId
            predicates.add(criteriaBuilder.equal(root.get("empleado").get("id"), empleadoId));

            if (fechaDesde != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("fechaInicio"), fechaDesde));
            }

            if (fechaHasta != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("fechaFin"), fechaHasta));
            }

            // Por defecto, ordenar por fecha de inicio descendente (más recientes primero)
            // El orden se define en el Pageable que se pasa al repositorio
            // query.orderBy(criteriaBuilder.desc(root.get("fechaInicio")));


            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}