package org.grupo_h.comun.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad que representa una notificación para un empleado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notificaciones") // Nombre de la tabla en la base de datos
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_notificacion")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY) // LAZY para no cargar el empleado a menos que se necesite explícitamente
    @JoinColumn(name = "empleado_destino_id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_NOTIFICACION_EMPLEADO_DEST"))
    private Empleado empleadoDestino;

    @Column(nullable = false, length = 1000) // Aumentar longitud si los mensajes pueden ser largos
    private String mensaje;

    @Column(nullable = false)
    private LocalDateTime fechaHoraCreacion;

    @Column(nullable = false)
    private boolean leida = false;

    /**
     * Tipo de evento que generó la notificación.
     * Ejemplos: "NUEVA_SOLICITUD_COLABORACION", "SOLICITUD_ACEPTADA", "NUEVO_PRODUCTO_CATALOGO".
     */
    @Column(length = 100)
    private String tipoEvento;

    /**
     * ID de la entidad relacionada con el evento (ej. ID de SolicitudColaboracion, ID de Producto).
     * Puede ser null si la notificación no se refiere a una entidad específica.
     */
    private UUID idReferenciaEntidad;

    /**
     * URL relativa para enlazar directamente al detalle de la entidad relacionada, si aplica.
     * Ejemplo: "/empleados/colaboraciones/solicitudes/recibidas", "/productos/detalle/{idReferenciaEntidad}"
     */
    @Column(length = 255)
    private String urlReferencia;

    @PrePersist
    protected void onCreate() {
        if (fechaHoraCreacion == null) {
            fechaHoraCreacion = LocalDateTime.now();
        }
    }

    // Constructores adicionales, getters y setters son generados por Lombok (@Data)
    // Puedes añadir lógica de negocio o helpers si es necesario.
}
