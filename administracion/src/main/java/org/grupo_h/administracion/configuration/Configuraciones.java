package org.grupo_h.administracion.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Properties;

/**
 * Configuración de seguridad y configuración de la aplicación.
 */
@Configuration
public class Configuraciones {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
    /**
     * Configura el codificador de contraseñas.
     *
     * @return El codificador de las contraseñas
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura la cadena de filtros de seguridad.
     *
     * @param http El objeto HttpSecurity para configurar la seguridad.
     * @return La cadena de filtros de seguridad.
     * @throws Exception Si ocurre una excepción durante la configuración.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())  // Deshabilitar CSRF para desarrollo (opcional, verificar en producción)
                .authorizeHttpRequests(authz -> authz
                        // Permite el acceso público al endpoint de registro
                        .requestMatchers(
                                "/administracion/**",
                                "/administrador/**",
                                "/css/**", "/js/**", "/img/**").permitAll()

                        .anyRequest().authenticated()
                );
//                .formLogin(Customizer.withDefaults()); // Habilita el login por defecto para otros endpoints protegidos

        return http.build();
    }
}
