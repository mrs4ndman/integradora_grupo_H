package org.grupo_h.empleados.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Properties;

/**
 * Configuración de seguridad y configuración de la aplicación.
 */
@Configuration
public class Configuraciones {
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
                                "/usuarios/**",
                                "/empleados/**",
                                "/sendMail",
                                "/prueba",
                                "/css/**", "/js/**", "/img/**").permitAll()

                        .anyRequest().authenticated()
                );
//                .formLogin(Customizer.withDefaults()); // Habilita el login por defecto para otros endpoints protegidos

        return http.build();
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername("recuperacontrasena.grupoh@gmail.com");
        mailSender.setPassword("jufb pfnt cdwn qzjt");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    @Bean
    public SimpleMailMessage templateSimpleMessage() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setText(
                "This is the test email template for your email:\n%s\n");
        return message;
    }



}
