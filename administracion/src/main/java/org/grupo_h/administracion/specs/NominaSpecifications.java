package org.grupo_h.administracion.specs;

import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.comun.entity.Nomina;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Order;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NominaSpecifications {

    public static Specification<Nomina> conFiltros(
            UUID empleadoId,
            String nombreEmpleadoCriterio, // Criterio de búsqueda por nombre/apellidos
            LocalDate periodoFechaDesde,
            LocalDate periodoFechaHasta) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Nomina, Empleado> empleadoJoin = root.join("empleado"); // Join para usar campos de Empleado

            if (empleadoId != null) {
                predicates.add(criteriaBuilder.equal(empleadoJoin.get("id"), empleadoId));
            } else if (nombreEmpleadoCriterio != null && !nombreEmpleadoCriterio.trim().isEmpty()) {
                String likePattern = "%" + nombreEmpleadoCriterio.toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(empleadoJoin.get("nombre")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(empleadoJoin.get("apellidos")), likePattern)
                ));
            }

            if (periodoFechaDesde != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("fechaInicio"), periodoFechaDesde));
            }

            if (periodoFechaHasta != null) {
                // Nóminas cuyo período (fechaInicio a fechaFin) se solapa con el rango de búsqueda.
                // Una nómina (nS, nE) se solapa con un rango de búsqueda (bS, bE) si:
                // (nS <= bE) y (nE >= bS)
                // Aquí, para "filtrando por período", consideraremos nóminas cuya fecha de inicio
                // esté dentro del rango especificado, o que su período se solape.
                // El PDF (p.6) dice "filtrando por empleado y por período".
                // Para una interpretación simple: nóminas cuya fechaInicio esté dentro del rango del filtro.
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("fechaInicio"), periodoFechaHasta));
                // O si queremos que la nómina esté completamente contenida:
                // predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("fechaFin"), periodoFechaHasta));
            }

            // Ordenación: más modernas a más antiguas (fechaInicio DESC), luego por nombre de empleado (apellidos ASC, nombre ASC)
            List<Order> orderList = new ArrayList<>();
            orderList.add(criteriaBuilder.desc(root.get("fechaInicio")));
            orderList.add(criteriaBuilder.asc(empleadoJoin.get("apellidos")));
            orderList.add(criteriaBuilder.asc(empleadoJoin.get("nombre")));
            query.orderBy(orderList);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}