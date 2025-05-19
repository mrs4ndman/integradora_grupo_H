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
import org.springframework.web.client.RestTemplate;

import java.util.Properties;

/**
 * Configuración de seguridad y configuración de la aplicación.
 */
@Configuration
public class Configuraciones {

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
                .authorizeHttpRequests(authorize -> authorize
                        // Rutas públicas: CSS, JS, imágenes, página de error, y todo el flujo de login/registro manual.
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/error/**").permitAll()
                        .requestMatchers("/",
                                "/usuarios/registro", "/usuarios/procesar-registro",
                                "/usuarios/inicio-sesion",
                                "/usuarios/inicio-sesion/usuario",
                                "/usuarios/inicio-sesion/password",
                                "/usuarios/inicio-sesion/autenticar", // Tu endpoint de login manual
                                "/usuarios/solicitar-reseteo-contrasena", "/usuarios/procesar-solicitud-reseteo",
                                "/usuarios/resetear-contrasena", "/usuarios/procesar-reseteo-contrasena",
                                "/usuarios/cambiar-password"
                        ).permitAll()
                        // Para las APIs REST y otras rutas de empleados/administracion:
                        // Ya NO podemos usar .hasRole() o .authenticated() de forma efectiva
                        // porque el SecurityContextHolder no se poblará de la manera estándar que estas reglas esperan
                        // (a menos que tu UsuarioController lo haga, pero dijiste NO UserDetails).
                        // La protección de estas rutas deberá hacerse programáticamente en los controladores
                        // o mediante interceptores que verifiquen tu atributo de sesión "emailAutenticado".
                        // Por ahora, para que Spring Security no bloquee todo por defecto si no hay Authentication,
                        // podrías permitir estas rutas base y manejar la seguridad en cada endpoint.
                        // O, si quieres que Spring Security al menos redirija al login si no hay sesión,
                        // puedes usar .anyRequest().permitAll() y tu controlador redirige si no hay "emailAutenticado".
                        // Una opción más segura es .anyRequest().authenticated() y luego manejar el 403/redirección
                        // si tu lógica de sesión no está presente.
                        // Vamos a mantener .anyRequest().authenticated() para que Spring Security redirija al login
                        // si se accede a algo no permitido sin una sesión de Spring (aunque nuestra sesión es manual).
                        .requestMatchers("/api/empleados/**").permitAll() // Permitir acceso a las APIs, la seguridad se hará en el controller
                        .requestMatchers("/empleados/**").permitAll()     // Permitir acceso a las UIs, la seguridad se hará en el controller
                        .requestMatchers("/administracion/**").permitAll() // Idem para admin
                        .requestMatchers("/usuarios/info").permitAll() // Idem para /info
                        .anyRequest().authenticated() // Para cualquier otra cosa no listada, que pida login (aunque no lo usemos)
                )
                // formLogin sigue siendo útil para definir la página de login a la que Spring Security redirige
                // si un filtro de seguridad (que no sea el de autenticación) deniega el acceso.
                .formLogin(form -> form
                        .loginPage("/usuarios/inicio-sesion") // A dónde redirigir si se necesita login
                        // El loginProcessingUrl ya no es relevante para tu flujo principal.
                        .loginProcessingUrl("/procesar-login-que-no-se-usa")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // Spring Security interceptará POST a /logout por defecto.
                        // Tu UsuarioController tiene un GET /logout que invalida la sesión.
                        // Esto está bien, el logout de Spring Security limpiará su propio contexto.
                        .logoutSuccessUrl("/usuarios/inicio-sesion?logout")
                        .invalidateHttpSession(true) // Importante
                        .deleteCookies("JSESSIONID") // Cookie de sesión estándar
                        .permitAll()
                )
                // CSRF: Es importante. Si lo habilitas, tus formularios POST y JS deben incluir el token.
                // Por ahora, para simplificar la depuración del login, lo mantenemos comentado.
                // Considera habilitarlo y gestionarlo una vez el login funcione.
                .csrf(csrf -> csrf.disable()); // DESHABILITADO TEMPORALMENTE PARA SIMPLIFICAR

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


    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
