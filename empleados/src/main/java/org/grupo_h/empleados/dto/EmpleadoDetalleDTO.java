package org.grupo_h.empleados.dto;

import lombok.Data;
import org.grupo_h.comun.entity.auxiliar.CuentaCorriente;
import org.grupo_h.comun.entity.auxiliar.Direccion;
import org.grupo_h.comun.entity.auxiliar.Genero;
import org.grupo_h.comun.entity.auxiliar.Pais;
import org.grupo_h.comun.entity.auxiliar.TarjetaCredito;
import org.grupo_h.comun.entity.auxiliar.TipoDocumento;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) que contiene información detallada de un empleado.
 * Utilizado para transferir datos completos de un empleado entre capas de la aplicación.
 */
@Data
public class EmpleadoDetalleDTO {
    // Datos personales
    private String nombre;
    private String apellidos;
    private byte[] fotografia;
    private Genero genero;
    private LocalDate fechaNacimiento;
    private Integer edad;
    private Pais paisNacimiento;
    private String comentarios;

    // Datos de contacto
    private TipoDocumento tipoDocumento;
    private String contenidoDocumento;
    private String prefijoTelefono;
    private String telefonoMovil;
    private Direccion direccion;

    // Archivo asociado
    private String archivoNombreOriginal;

    // Departamento
    private DepartamentoDTO departamento;

    // Especialidades
    private List<EspecialidadesEmpleadoDTO> especialidadesEmpleado;

    // Datos económicos
    private TarjetaCredito tarjetas;
    private Boolean aceptaInformacion;

    // Etiquetas
    private Set<EtiquetaDTO> etiquetas;

    // Relación con jefe y subordinados
    private EmpleadoDetalleSimpleDTO jefe;
    private Set<EmpleadoDetalleSimpleDTO> subordinados;

    // Información de cuenta corriente
    private CuentaCorriente cuentaCorriente;

}

// DTOs adicionales para las relaciones de Jefe y subordinados
@Data
class EmpleadoDetalleSimpleDTO {
    private UUID id;
    private String nombre;
    private String apellidos;
}
