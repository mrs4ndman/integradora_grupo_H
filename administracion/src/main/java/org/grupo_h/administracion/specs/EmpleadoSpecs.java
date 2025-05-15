package org.grupo_h.administracion.specs;

import org.grupo_h.comun.entity.Empleado;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class EmpleadoSpecs {
    public static Specification<Empleado> nombreContiene(String nombre) {
        return (root, query, cb) -> nombre == null || nombre.isEmpty()
                ? null
                : cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%");
    }

    public static Specification<Empleado> edadEntre(Integer edadMin, Integer edadMax) {
        return (root, query, cb) -> {
            if (edadMin == null || edadMax == null) return null;

            LocalDate hoy = LocalDate.now();
            LocalDate fechaMax = hoy.minusYears(edadMin); // el más joven
            LocalDate fechaMin = hoy.minusYears(edadMax); // el más mayor

            return cb.between(root.get("fechaNacimiento"), fechaMin, fechaMax);
        };
    }

    public static Specification<Empleado> departamentoContiene(UUID departamentoId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("departamento").get("id"), departamentoId);
    }

    public static Specification<Empleado> departamentosEnLista(List<UUID> idsDepartamento) {
        return (root, query, criteriaBuilder) ->
                root.get("departamento").get("id").in(idsDepartamento);
    }

    public static Specification<Empleado> numeroDocumentoContiene(String numeroDni) {
        return (root, query, cb) -> {
            if (numeroDni == null || numeroDni.trim().isEmpty()) {
                return null;
            }
            return cb.like(cb.lower(root.get("numeroDocumento")), "%" + numeroDni.trim().toLowerCase() + "%");
        };
    }



}

