package org.grupo_h.administracion.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

import java.util.Properties;

/**
 * Configuración de seguridad y configuración de la aplicación.
 */
@Configuration
public class Configuraciones {

    /**
     * Configura un RestTemplate para realizar llamadas HTTP a APIs externas.
     *
     * @param builder El RestTemplateBuilder que se utilizará para construir el RestTemplate
     * @return Una instancia de RestTemplate configurada
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    /**
     * Proporciona un codificador de contraseñas BCrypt para la aplicación.
     * BCrypt implementa un algoritmo de hash seguro para contraseñas con salt incorporado.
     *
     * @return Una instancia de BCryptPasswordEncoder para el cifrado de contraseñas
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    /**
     * Instancia el ModelMapper para su uso en este módulo
     *
     * @return El Bean de ModelMapper para poder instanciarlo
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
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
                        .requestMatchers(HttpMethod.POST, "/api/administrador/productos/borrar/**").authenticated()

                        // Permite el acceso público al endpoint de registro
                        .requestMatchers(
                                "/administracion/**",
                                "/administrador/**",
                                "/api/**",
                                "/css/**", "/js/**", "/img/**").permitAll()

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/administrador/inicio-sesion")); // Habilita el login por defecto para otros endpoints protegidos

        return http.build();
    }
}
