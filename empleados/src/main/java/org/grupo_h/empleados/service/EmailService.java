package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.auxiliar.EmailDetalles;

public interface EmailService {
    // Enviar un email sencillo
    String enviarEmail(EmailDetalles details);
}
