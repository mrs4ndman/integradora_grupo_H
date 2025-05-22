package org.grupo_h.empleados.configuration;

import org.grupo_h.empleados.component.PaginaVisitaInterceptor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;

/**
 * Configuración de seguridad y configuración de la aplicación.
 */
@Configuration
public class Configuraciones implements WebMvcConfigurer {

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
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Configuración CORS (importante si PHP llama a APIs desde JS)
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        // ----- RUTAS PÚBLICAS -----
                        // Estas son las rutas que CUALQUIERA puede acceder
                        .requestMatchers(
                                "/usuarios/inicio-sesion",      // Página inicial de login
                                "/usuarios/inicio-sesion/**",   // Para los sub-pasos /usuario y /password del login
                                "/usuarios/registro",           // Formulario y procesamiento de registro
                                "/usuarios/logout",             // Endpoint de logout
                                "/usuarios/cambiar-password",   // Para el flujo de olvido/reseteo de contraseña
                                // "/sendMail",                 // Si es un endpoint público para pruebas, si no, proteger
                                // "/prueba",                   // Si es una página de prueba pública, si no, proteger
                                "/css/**", "/js/**", "/img/**"  // Recursos estáticos siempre públicos
                        ).permitAll()

                        // ----- RUTAS QUE REQUIEREN AUTENTICACIÓN -----
                        // Todas las rutas bajo /empleados/ (excepto las públicas definidas arriba si las hubiera)
                        // y todas las rutas bajo /api/empleados/ requerirán que el usuario esté logueado.
                        .requestMatchers(
                                "/empleados/**",                 // Todas las páginas de empleados
                                "/api/empleados/**"              // Todas las APIs del módulo empleados
                                // (incluyendo /api/empleados/area-personal/info)
                        ).authenticated()

                        // ----- CUALQUIER OTRA PETICIÓN (si no coincide con nada anterior) -----
                        // Por seguridad, es buena práctica que cualquier ruta no definida explícitamente
                        // requiera autenticación.
                        .anyRequest().authenticated()
                )
                // ----- CONFIGURACIÓN DEL FORMULARIO DE LOGIN -----
                // Esto le dice a Spring Security:
                // 1. Cuál es tu página de login (para redirigir si alguien intenta acceder a algo protegido sin sesión).
                // 2. Dónde procesar el envío del formulario de login (Spring maneja esto por defecto con /login si no lo especificas).
                // 3. A dónde ir después de un login exitoso.
                // 4. A dónde ir si el login falla.
                .formLogin(form -> form
                        .loginPage("/usuarios/inicio-sesion") // Tu página Thymeleaf para el paso de email
                        // Spring Boot por defecto maneja POST a /login para procesar la autenticación.
                        // Tu UsuarioController maneja el login en pasos, lo cual es un poco diferente al
                        // formLogin estándar de Spring Security. Spring Security interceptará las peticiones
                        // a recursos protegidos y redirigirá a /usuarios/inicio-sesion.
                        // El proceso de login que tienes en UsuarioController seguirá funcionando para los usuarios
                        // que lleguen a /usuarios/inicio-sesion.
                        .defaultSuccessUrl("/usuarios/info", true) // A dónde ir después de que Spring Security maneje un login exitoso (si se usara su propio flujo)
                        .failureUrl("/usuarios/inicio-sesion?error=true") // A dónde ir si el login manejado por Spring Security falla
                )
                // ----- CONFIGURACIÓN DE LOGOUT -----
                .logout(logout -> logout
                        .logoutUrl("/usuarios/logout_spring_security") // Puedes definir un endpoint diferente para el logout de Spring Security
                        .logoutSuccessUrl("/usuarios/inicio-sesion?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("EMPLOYEE_SESSION", "remember-me-token") // Asegúrate de borrar tus cookies
                );
        return http.build();
    }

    // Bean para la configuración CORS
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8081")); // Puerto de tu Apache/PHP
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*")); // Considera ser más específico en producción
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica esta configuración a las rutas de tu API que serán llamadas desde PHP
        source.registerCorsConfiguration("/api/empleados/**", configuration);
        return source;
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


    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Autowired
    private PaginaVisitaInterceptor paginaVisitaInterceptor;

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
        registry.addInterceptor(paginaVisitaInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/js/**", "/images/**", "/api/**", "/usuarios/inicio-sesion/**", "/usuarios/logout", "/empleados/historial");
    }

}
