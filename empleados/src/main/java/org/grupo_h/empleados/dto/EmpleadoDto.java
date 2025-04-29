package org.grupo_h.empleados.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.grupo_h.comun.entity.DatosEconomicos;
import org.grupo_h.comun.entity.Departamento;
import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.comun.entity.EspecialidadesEmpleado;
import org.grupo_h.comun.entity.auxiliar.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link Empleado}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Value
public class EmpleadoDto implements Serializable {
    String nombre;
    String apellidos;
    byte[] fotografia;
    String  genero;
    LocalDate fechaNacimiento;
    Integer edad;
    Pais paisNacimiento;
    String comentarios;
    TipoDocumento tipoDocumento;
    String contenidoDocumento;
    String prefijoTelefono;
    String telefonoMovil;
    Direccion direccion;
    Departamento departamento;
    List<EspecialidadesEmpleado> especialidadesEmpleado;
    DatosEconomicos datosEconomicos;
    List<TarjetaCredito> tarjetasCredito;
    Boolean aceptaInformacion;


}