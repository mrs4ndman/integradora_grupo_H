package org.grupo_h.administracion.service;

import org.grupo_h.administracion.dto.EmpleadoConsultaDTO;
import org.grupo_h.administracion.dto.EmpleadoDTO;
import org.grupo_h.administracion.dto.EmpleadoDetalleDTO;
import org.grupo_h.administracion.dto.EmpleadoSimpleDTO;
import org.grupo_h.comun.entity.Empleado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio para gestionar operaciones relacionadas con empleados.
 * Proporciona métodos para obtener y manipular información de empleados.
 */
@Service
public interface EmpleadoService {
    /**
     * Obtiene información detallada de un empleado por su identificador.
     *
     * @param id Identificador único del empleado
     * @return Optional con los detalles del empleado si existe, o vacío si no se encuentra
     */
    Optional<EmpleadoDetalleDTO> obtenerDetalleEmpleado(UUID id);

    void asignarSubordinado(UUID idJefe, UUID idSubordinado);

    void desasignarSubordinadoDeSuJefe(UUID idSubordinado);

    List<EmpleadoSimpleDTO> obtenerTodosLosEmpleadosParaSeleccion();

    List<EmpleadoDTO> buscarEmpleados(EmpleadoConsultaDTO filtro);

    Page<EmpleadoDTO> buscarEmpleadosPaginados(EmpleadoConsultaDTO filtro, int pagina, int tamanio);

    Optional<Empleado> buscarPorDni(String dni);

    Optional<Empleado> buscarEmpleado(UUID empleadoID);

    void eliminarPorId(UUID id);

    void eliminarPorDni(String dni);
}
