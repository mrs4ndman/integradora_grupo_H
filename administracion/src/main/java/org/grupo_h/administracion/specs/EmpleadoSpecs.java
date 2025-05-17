package org.grupo_h.administracion.specs;

import org.grupo_h.comun.entity.Empleado; // Asegúrate que la entidad Empleado tiene el campo 'activo'
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate; // Importa Predicate si usas cb.isTrue o cb.isFalse

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class EmpleadoSpecs {

    public static Specification<Empleado> esActivo() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("activo"));
    }

    public static Specification<Empleado> esInactivo() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isFalse(root.get("activo"));
    }

    public static Specification<Empleado> nombreContiene(String nombre) {
        return (root, query, cb) -> (nombre == null || nombre.isEmpty())
                ? null
                : cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%");
    }

    public static Specification<Empleado> apellidosContiene(String apellidos) {
        return (root, query, cb) -> (apellidos == null || apellidos.isEmpty())
                ? null
                : cb.like(cb.lower(root.get("apellidos")), "%" + apellidos.toLowerCase() + "%");
    }


    public static Specification<Empleado> edadEntre(Integer edadMin, Integer edadMax) {
        return (root, query, cb) -> {
            if (edadMin == null || edadMax == null || edadMin < 0 || edadMax < 0 || edadMin > edadMax) {
                return null;
            }

            LocalDate hoy = LocalDate.now();
            LocalDate fechaNacimientoMaxJoven = hoy.minusYears(edadMin).minusDays(1); // Persona que cumple edadMin mañana
            LocalDate fechaNacimientoMinMayor = hoy.minusYears(edadMax + 1).plusDays(1); // Persona que cumplió edadMax ayer


            Predicate p = cb.conjunction(); // Un predicado que siempre es true, para añadir condiciones opcionales

            if (root.get("fechaNacimiento").getJavaType() == LocalDate.class) {
                p = cb.and(p, cb.between(root.get("fechaNacimiento"), fechaNacimientoMinMayor, fechaNacimientoMaxJoven));
            }
            return p;
        };
    }

    public static Specification<Empleado> departamentoContiene(UUID departamentoId) {
        return (root, query, criteriaBuilder) -> (departamentoId == null)
                ? null
                : criteriaBuilder.equal(root.get("departamento").get("id"), departamentoId);
    }

    public static Specification<Empleado> departamentosEnLista(List<UUID> idsDepartamento) {
        return (root, query, criteriaBuilder) -> (idsDepartamento == null || idsDepartamento.isEmpty())
                ? null
                : root.get("departamento").get("id").in(idsDepartamento);
    }

    public static Specification<Empleado> numeroDocumentoContiene(String numeroDni) {
        return (root, query, cb) -> {
            if (numeroDni == null || numeroDni.trim().isEmpty()) {
                return null;
            }
            return cb.like(cb.lower(root.get("numeroDocumento")), "%" + numeroDni.toLowerCase() + "%");
        };
    }

    public static Specification<Empleado> numeroDocumentoEs(String numeroDni) {
        return (root, query, cb) -> {
            if (numeroDni == null || numeroDni.trim().isEmpty()) {
                return null;
            }
            return cb.equal(cb.lower(root.get("numeroDocumento")), numeroDni.toLowerCase());
        };
    }
}