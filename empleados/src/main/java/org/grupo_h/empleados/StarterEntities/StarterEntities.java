package org.grupo_h.empleados.StarterEntities;

import jakarta.annotation.PostConstruct;
import org.grupo_h.comun.entity.Genero;
import org.grupo_h.empleados.repository.GeneroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StarterEntities {

    @Autowired
    private GeneroRepository generoRepository;

    @PostConstruct
    public void init() {
        if (generoRepository.count() == 0) {
            generoRepository.save(new Genero("M", "Masculino"));
            generoRepository.save(new Genero("F", "Femenino"));
            generoRepository.save(new Genero("O", "Otro"));
        }
    }
}
