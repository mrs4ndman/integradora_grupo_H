package org.grupo_h.comun.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.grupo_h.comun.entity.auxiliar.CuentaCorriente;
import org.grupo_h.comun.entity.auxiliar.Direccion;
import org.grupo_h.comun.entity.chat.Colaboracion;
import org.grupo_h.comun.entity.chat.Mensaje;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false)
    private String email;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

//    @OneToOne(mappedBy = "empleado")
//    private Usuario usuario;

    @Embedded
    private Direccion direccion;

    @Embedded
    private CuentaCorriente cuentaCorriente;

    @Column(name = "fecha_alta_en_BD")
    private LocalDate fechaAltaEnBaseDeDatos = LocalDate.now();

}
