package org.grupo_h.comun.StarterEntities;

import jakarta.annotation.PostConstruct;
import org.grupo_h.comun.Repositories.GeneroRepository;
import org.grupo_h.comun.Repositories.PaisRepository;
import org.grupo_h.comun.Repositories.TipoViaRepository;
import org.grupo_h.comun.entity.auxiliar.Genero;
import org.grupo_h.comun.entity.auxiliar.Pais;
import org.grupo_h.comun.entity.auxiliar.TipoVia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StarterEntities {

    @Autowired
    private GeneroRepository generoRepository;

    @Autowired
    private PaisRepository paisRepository;

    @Autowired
    private TipoViaRepository tipoViaRepository;

    @PostConstruct
    public void initGenero() {
        if (generoRepository.count() == 0) {
            generoRepository.save(new Genero("M", "Masculino"));
            generoRepository.save(new Genero("F", "Femenino"));
            generoRepository.save(new Genero("O", "Otro"));
        }
    }

    @PostConstruct
    public void initPais() {
        if (paisRepository.count() == 0) {
            paisRepository.save(new Pais("ESP", "España"));
            paisRepository.save(new Pais("FRA", "Francia"));
            paisRepository.save(new Pais("USA", "Estados Unidos"));
            paisRepository.save(new Pais("ITA", "Italia"));
            paisRepository.save(new Pais("AND", "Andorra"));
        }
    }

    @PostConstruct
    public void initTipoVia() {
        if (tipoViaRepository.count() == 0) {
            tipoViaRepository.save(new TipoVia("AV", "Avenida"));
            tipoViaRepository.save(new TipoVia("C", "Calle"));
            tipoViaRepository.save(new TipoVia("PS", "Paseo"));
            tipoViaRepository.save(new TipoVia("PA", "Pasaje"));
        }
    }
}
