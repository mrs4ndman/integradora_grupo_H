package org.grupo_h.administracion.dto;

import lombok.Data;
import org.grupo_h.comun.entity.auxiliar.CuentaCorriente;
import org.grupo_h.comun.entity.auxiliar.Direccion;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class EmpleadoDetalleDTO {
    private UUID id;
    private String nombre;
    private String apellido;
    private String email;
    private LocalDate fechaNacimiento;
    private Direccion direccion;
    private CuentaCorriente cuentaCorriente;
    private LocalDate fechaAltaEnBaseDeDatos;
}

