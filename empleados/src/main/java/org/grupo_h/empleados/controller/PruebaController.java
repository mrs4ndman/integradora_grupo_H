package org.grupo_h.empleados.controller;

import org.grupo_h.comun.Repositories.GeneroRepository;
import org.grupo_h.comun.Repositories.PaisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PruebaController {

    @Autowired
    private PaisRepository paisRepository;

    @Autowired
    private GeneroRepository generoRepository;


    @GetMapping("prueba")
    public String index(Model model) {
        model.addAttribute("generos", generoRepository.findAll());
        model.addAttribute("pais", paisRepository.findAll());
        return "prueba.html";
    }
}
