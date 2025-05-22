package org.grupo_h.empleados.service;

import org.grupo_h.empleados.dto.NominaEmpleadoDetalleDTO;
import org.grupo_h.empleados.dto.NominaEmpleadoResumenDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface NominaEmpleadoService {

    Optional<NominaEmpleadoDetalleDTO> obtenerDetalleNominaPorIdParaEmpleado(UUID idNomina, String emailEmpleado);

    Page<NominaEmpleadoResumenDTO> consultarNominasPorEmpleado(String emailEmpleado,
                                                               LocalDate fechaDesde,
                                                               LocalDate fechaHasta,
                                                               Pageable pageable);

    byte[] generarPdfNomina(UUID idNomina, String emailEmpleado) throws Exception;
}