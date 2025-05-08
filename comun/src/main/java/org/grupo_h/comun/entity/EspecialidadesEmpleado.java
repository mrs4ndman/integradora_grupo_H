package org.grupo_h.comun.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class EspecialidadesEmpleado {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name="Especialidad")
    private String especialidad;


//    private EspecialidadesEmpleado(EspecialidadesEmpleado e) {
//        this.id = e.id;
//        this.especialidad = e.especialidad;
//    }
//
//    public static EspecialidadesEmpleado of(String especialidad) {
//        EspecialidadesEmpleado nuevaEspecialidad = new EspecialidadesEmpleado();  // Asignar un ID único
//        nuevaEspecialidad.setEspecialidad(especialidad);  // Asignar el nombre de la especialidad
//        return nuevaEspecialidad;
//    }
}

