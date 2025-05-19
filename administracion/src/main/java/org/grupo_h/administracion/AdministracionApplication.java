package org.grupo_h.administracion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
// Escanear entidades en el módulo Comun
@EntityScan("org.grupo_h.comun.entity")
// Escanear repositorios en el módulo Comun
@EnableJpaRepositories(basePackages = {"org.grupo_h.comun.repository"})
// Escanear componentes en el módulo Comun y empleados
// Es muy importante para que muestre los templates de Thymeleaf
@ComponentScan(basePackages = {"org.grupo_h.comun", "org.grupo_h.administracion"})
public class AdministracionApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(AdministracionApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(AdministracionApplication.class);
	}
}
