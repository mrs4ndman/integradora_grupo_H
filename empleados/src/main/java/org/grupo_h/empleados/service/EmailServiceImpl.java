package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.auxiliar.EmailDetalles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
        @Autowired private JavaMailSender javaMailSender;

        @Value("recuperacontrasena.grupoh@gmail.com") private String sender;

        public String enviarEmail(EmailDetalles detalles)
        {
            try {
                // Creando un objeto de correo sencillo
                SimpleMailMessage mailMessage
                        = new SimpleMailMessage();

                // Configurando los detalles necesarios
                mailMessage.setFrom(sender);
                mailMessage.setTo(detalles.getDestinatario());
                mailMessage.setText(detalles.getCuerpoMsg());
                mailMessage.setSubject(detalles.getAsunto());

                // Enviando el correo
                javaMailSender.send(mailMessage);
                return "Correo enviado correctamente";
            }
            catch (Exception e) {
                return "Se ha producido un error al enviar el correo: " + e.getMessage();
            }
        }
}
