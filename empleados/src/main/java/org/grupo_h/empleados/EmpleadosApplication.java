package org.grupo_h.empleados;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.RepositoryDefinition;

@SpringBootApplication
// Escanear entidades en el módulo Comun
@EntityScan("org.grupo_h.comun.entity")
//@EnableJpaRepositories(basePackages = {"org.grupo_h.comun.Repositories"})
//@ComponentScan(basePackages = {"org.grupo_h.comun"})
public class EmpleadosApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmpleadosApplication.class, args);
	}

}
