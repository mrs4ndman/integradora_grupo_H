package org.grupo_h.comun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
// Escanear entidades en el módulo Comun
@EntityScan("org.grupo_h.comun.entity")
public class ComunApplication {

    public static void main(String[] args) {
        SpringApplication.run(ComunApplication.class, args);
    }

}
