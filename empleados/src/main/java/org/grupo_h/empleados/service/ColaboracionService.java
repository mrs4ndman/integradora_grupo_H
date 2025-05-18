package org.grupo_h.empleados.service;

import org.grupo_h.empleados.dto.EmpleadoSimpleDTO;
import org.grupo_h.empleados.dto.colaboracion.ColaboracionResponseDTO;
import org.grupo_h.empleados.dto.colaboracion.DecisionSolicitudRequestDTO;
import org.grupo_h.empleados.dto.colaboracion.SolicitudColaboracionRequestDTO;
import org.grupo_h.empleados.dto.colaboracion.SolicitudColaboracionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface ColaboracionService {

    /**
     * Crea una nueva solicitud de colaboración.
     *
     * @param requestDTO DTO con los datos de la solicitud.
     * @param emailEmpleadoEmisor Email del empleado que emite la solicitud.
     * @return DTO de la solicitud creada.
     * @throws org.grupo_h.comun.exceptions.ResourceNotFoundException si el empleado emisor o receptor no se encuentran.
     * @throws IllegalStateException si ya existe una colaboración activa o una solicitud pendiente.
     * @throws IllegalArgumentException si el emisor y el receptor son el mismo.
     */
    SolicitudColaboracionResponseDTO crearSolicitud(SolicitudColaboracionRequestDTO requestDTO, String emailEmpleadoEmisor);

    /**
     * Lista las solicitudes de colaboración emitidas por un empleado.
     *
     * @param emailEmpleadoEmisor Email del empleado emisor.
     * @param pageable Configuración de paginación.
     * @return Página de DTOs de solicitudes emitidas.
     */
    Page<SolicitudColaboracionResponseDTO> listarSolicitudesEmitidas(String emailEmpleadoEmisor, Pageable pageable);

    /**
     * Lista las solicitudes de colaboración recibidas por un empleado.
     *
     * @param emailEmpleadoReceptor Email del empleado receptor.
     * @param pageable Configuración de paginación.
     * @return Página de DTOs de solicitudes recibidas.
     */
    Page<SolicitudColaboracionResponseDTO> listarSolicitudesRecibidas(String emailEmpleadoReceptor, Pageable pageable);

    /**
     * Lista las solicitudes de colaboración PENDIENTES recibidas por un empleado.
     *
     * @param emailEmpleadoReceptor Email del empleado receptor.
     * @param pageable Configuración de paginación.
     * @return Página de DTOs de solicitudes pendientes recibidas.
     */
    Page<SolicitudColaboracionResponseDTO> listarSolicitudesPendientesRecibidas(String emailEmpleadoReceptor, Pageable pageable);

    /**
     * Cuenta las solicitudes de colaboración PENDIENTES recibidas por un empleado.
     * @param emailEmpleadoReceptor Email del empleado receptor.
     * @return Número de solicitudes pendientes.
     */
    long contarSolicitudesPendientesRecibidas(String emailEmpleadoReceptor);


    /**
     * Gestiona una solicitud de colaboración (aceptar o rechazar).
     *
     * @param idSolicitud ID de la solicitud a gestionar.
     * @param decisionDTO DTO con la decisión (ACEPTAR/RECHAZAR) y un mensaje opcional.
     * @param emailEmpleadoReceptor Email del empleado que toma la decisión (debe ser el receptor original).
     * @return DTO de la solicitud actualizada.
     * @throws org.grupo_h.comun.exceptions.ResourceNotFoundException si la solicitud no se encuentra.
     * @throws org.springframework.security.access.AccessDeniedException si el empleado no es el receptor de la solicitud.
     * @throws IllegalStateException si la solicitud no está en estado PENDING.
     */
    SolicitudColaboracionResponseDTO gestionarSolicitud(UUID idSolicitud, DecisionSolicitudRequestDTO decisionDTO, String emailEmpleadoReceptor);

    /**
     * Cancela una colaboración que está actualmente en curso.
     *
     * @param idColaboracion ID de la colaboración a cancelar.
     * @param emailEmpleadoCancelador Email del empleado que solicita la cancelación (debe ser parte de la colaboración).
     * @return DTO de la colaboración actualizada (ahora INACTIVA).
     * @throws org.grupo_h.comun.exceptions.ResourceNotFoundException si la colaboración no se encuentra o no está activa.
     * @throws org.springframework.security.access.AccessDeniedException si el empleado no forma parte de la colaboración.
     */
    ColaboracionResponseDTO cancelarColaboracionEnCurso(UUID idColaboracion, String emailEmpleadoCancelador);

    /**
     * Lista las colaboraciones activas para un empleado.
     *
     * @param emailEmpleado Email del empleado.
     * @param pageable Configuración de paginación.
     * @return Página de DTOs de colaboraciones activas.
     */
    Page<ColaboracionResponseDTO> listarColaboracionesActivas(String emailEmpleado, Pageable pageable);

    @Transactional(readOnly = true)
    Page<EmpleadoSimpleDTO> buscarEmpleadosParaColaboracion(String emailEmpleadoSolicitante, String terminoBusqueda, Pageable pageable);
}