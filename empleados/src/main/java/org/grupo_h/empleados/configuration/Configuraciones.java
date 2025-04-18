package org.grupo_h.empleados.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class Configuraciones {
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())  // Deshabilitar CSRF para desarrollo (opcional, verificar en producción)
                .authorizeHttpRequests(authz -> authz
                        // Permite el acceso público al endpoint de registro
                        .requestMatchers(
                                "/usuarios/registro",
                                "/empleados/registro-datos",
                                "/empleados/registro-direccion",
                                "/empleados/registro-financiero",
                                "/empleados/registro-finales",
                                "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults()); // Habilita el login por defecto para otros endpoints protegidos

        return http.build();
    }
}

