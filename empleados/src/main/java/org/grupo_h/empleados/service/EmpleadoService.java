package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.comun.entity.Usuario;
import org.grupo_h.empleados.dto.EmpleadoDetalleDTO;
import org.grupo_h.empleados.dto.EmpleadoRegistroDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public interface EmpleadoService {
    Empleado registrarEmpleado(EmpleadoRegistroDTO empleadoDTO, UUID IdUsuario, byte[] foto) throws Exception;

    Optional<Empleado> obtenerEmpleadoPorId(UUID id);

    Optional<Empleado> findByNombreEmpleado(String nombreEmpleado);

    Optional<EmpleadoDetalleDTO> obtenerDetalleEmpleado(UUID id);

    List<Empleado> getSubordinadosDelJefeAutenticado() throws AccessDeniedException;

    List<Empleado> getSubordinadosByJefeId(UUID jefeId);

    void asignarEtiquetaASubordinado(UUID jefeId, UUID subordinadoId, UUID etiquetaId) throws AccessDeniedException;
    void asignarEtiquetaAUltiplesSubordinados(UUID jefeId, List<UUID> subordinadoIds, UUID etiquetaId) throws AccessDeniedException;
    void actualizarEtiquetasSubordinado(UUID jefeId, UUID subordinadoId, List<UUID> etiquetaIdsAMantener) throws AccessDeniedException;

    void eliminarEtiquetaDeSubordinado(UUID subordinadoId, UUID etiquetaId) throws AccessDeniedException;

    void asignarEtiquetasMasivo(UUID jefeId, List<UUID> empleadoIds, List<UUID> etiquetaIds) throws AccessDeniedException; // Modificado para incluir jefeId

    boolean esJefeDirecto(UUID subordinadoId) throws AccessDeniedException;

    Optional<EmpleadoDetalleDTO> findEmpleadoDetalleById(UUID subordinadoId);

    boolean esJefeDirecto(UUID jefeId, UUID subordinadoId);
}
