package org.grupo_h.empleados;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
// Escanear entidades en el módulo Comun
@EntityScan("org.grupo_h.comun.entity")
// Escanear repositorios en el módulo Comun
@EnableJpaRepositories(basePackages = {"org.grupo_h.comun.repository"})
// Escanear componentes en el módulo Comun y empleados
// Es muy importante para que muestre los templates de Thymeleaf
@ComponentScan(basePackages = {"org.grupo_h.comun", "org.grupo_h.empleados"})
public class EmpleadosApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmpleadosApplication.class, args);
	}

}
