package org.grupo_h.comun.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class ConfiguracionesComun {
    /**
     * Configura el codificador de contraseñas.
     *
     * @return El codificador de las contraseñas
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
