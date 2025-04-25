package org.grupo_h.empleados.controller;

import org.grupo_h.comun.entity.auxiliar.EmailDetalles;
import org.grupo_h.empleados.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class EmailController {

    @Autowired
    private EmailService emailService;

    // Enviar email sencillo
    @PostMapping("/sendMail")
    public String sendMail(@RequestBody EmailDetalles detalles)
    {
        String status
                = emailService.enviarEmail(detalles);

        return status;
    }
}