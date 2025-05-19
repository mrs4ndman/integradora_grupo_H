package org.grupo_h.empleados;

import org.apache.catalina.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.RepositoryDefinition;

@SpringBootApplication
// Escanear entidades en el módulo Comun
@EntityScan("org.grupo_h.comun.entity")
// Escanear repositorios en el módulo Comun
@EnableJpaRepositories(basePackages = {"org.grupo_h.comun.repository"})
// Escanear componentes en el módulo Comun y empleados
// Es muy importante para que muestre los templates de Thymeleaf
@ComponentScan(basePackages = {"org.grupo_h.comun", "org.grupo_h.empleados"})
public class EmpleadosApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(EmpleadosApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(EmpleadosApplication.class);
	}

}
