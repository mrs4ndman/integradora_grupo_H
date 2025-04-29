package org.grupo_h.administracion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.grupo_h.comun.entity.Usuario;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuariosForm {
    private List<Usuario> usuariosActuales;
    // getters / setters
}
