package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.auxiliar.Genero;
import org.grupo_h.comun.repository.GeneroRepository;
import org.grupo_h.empleados.dto.EmpleadoRegistroDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GeneroServiceImpl implements GeneroService {

    @Autowired
    private GeneroRepository generoRepository;

    // Implementación del método para obtener la lista de géneros de manera limpia
    // Evitar tener que Castear generoRepository en el controlador
    @Override
    public List<Genero> obtenerGeneros() {
        List<Genero> generos = new ArrayList<>();
        generoRepository.findAll().forEach(generos::add); // Convertir Iterable a List
        return generos;
    }

    // Implementación del método para asignar el primer género si el campo generoSeleccionado es nulo
    @Override
    public void asignarPrimerGeneroSiEsNulo(EmpleadoRegistroDTO empleadoRegistroDTO, List<Genero> generos) {
        if (empleadoRegistroDTO.getGeneroSeleccionado() == null && !generos.isEmpty()) {
            empleadoRegistroDTO.setGeneroSeleccionado(generos.get(0)); // Asignar el primer objeto Genero
        }
    }
}
