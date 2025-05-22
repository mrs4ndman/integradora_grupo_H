package org.grupo_h.empleados.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.grupo_h.comun.entity.LineaNomina; // Asumiendo que tienes un DTO para LineaNomina o usas la entidad directamente

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NominaEmpleadoDetalleDTO {
    private UUID id;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String numeroSeguridadSocialEmpleado;
    private String puestoEmpleadoNomina;
    private List<LineaNominaDTO> lineas; // Necesitarás un LineaNominaDTO
    private Double totalDevengos;
    private Double totalDeducciones;
    private Double salarioNeto;
    private Map<String, String> empresaParametros; // Para datos de la empresa
    private EmpleadoSimpleConDNIDTO empleado; // Un DTO simple del empleado

    // Acumulados anuales
    private Double cantidadBrutaAcumuladaAnual;
    private Double cantidadPercibidaAcumuladaAnual;
    private Double retencionesAcumuladasAnual;
    private Integer anioEjercicio;
}