package org.grupo_h.administracion.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductoModificacionDTO {
    @Size(max = 255, message = "La descripción no puede tener mas de 255 caracteres.")
    private String descripcion;
    @DecimalMin(value = "0.01", message = "El precio debe ser un valor positivo mayor que cero.")
    private BigDecimal precio;
    @Size(max = 100, message = "La marca no puede tener mas de 100 caracteres.")
    private String marca;
    @Min(value = 0, message = "Las unidades no pueden tener un valor negativo.")
    private Integer unidades;
    @NotNull(message = "Debe seleccionar una categoría.")
    private List<UUID> categoriasIds;
    @Past(message = "La fecha de fabricación debe ser anterior a la fecha actual.")
    private LocalDate fechaFabricacion;
    private Boolean esPerecedero;
}