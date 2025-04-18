package org.grupo_h.empleados.dto;

import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class EmpleadoRegistroDTO {

    private UUID id;

    @NotBlank
    private String nombre;

    @NotBlank
    private String apellido;

    @DateTimeFormat( pattern = "dd/MM/yyyy")
    @Past(message = "{Validacion.fechaNac.Past}")
    private LocalDate fechaNacimiento;

    @NotBlank
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "{Vaidacion.email.pattern}"
    )
    private String email;

    @Valid
    private DireccionDTO direccion;

    @Valid
    private CuentaCorrienteDTO cuentaCorriente;


}
