package org.grupo_h.empleados.controller;

import org.grupo_h.empleados.dto.NominaEmpleadoDetalleDTO;
import org.grupo_h.empleados.service.NominaEmpleadoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/empleados/nominas")
public class NominaEmpleadoController {

    private static final Logger logger = LoggerFactory.getLogger(NominaEmpleadoController.class);

    private final NominaEmpleadoService nominaEmpleadoService;

    @Autowired
    public NominaEmpleadoController(NominaEmpleadoService nominaEmpleadoService) {
        this.nominaEmpleadoService = nominaEmpleadoService;
    }

    private String getEmailAutenticado(HttpSession session) {
        String email = (String) session.getAttribute("emailAutenticado");
        if (email == null || email.isEmpty()) {
            throw new AccessDeniedException("Usuario no autenticado.");
        }
        return email;
    }

    @GetMapping("/mis-nominas")
    public String mostrarMisNominas(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        try {
            String emailEmpleado = getEmailAutenticado(session);
            model.addAttribute("currentUserEmail", emailEmpleado); // Para el JavaScript
            // La carga de datos la hará el JavaScript vía API
            return "nomina/mis-nominas-empleado"; // Nueva plantilla para el empleado
        } catch (AccessDeniedException e) {
            redirectAttributes.addFlashAttribute("error", "Debe iniciar sesión para ver sus nóminas.");
            return "redirect:/usuarios/inicio-sesion";
        }
    }

    @GetMapping("/{idNomina}")
    public String verDetalleNomina(@PathVariable UUID idNomina,
                                   Model model,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        try {
            String emailEmpleado = getEmailAutenticado(session);
            Optional<NominaEmpleadoDetalleDTO> nominaOpt = nominaEmpleadoService.obtenerDetalleNominaPorIdParaEmpleado(idNomina, emailEmpleado);

            if (nominaOpt.isPresent()) {
                model.addAttribute("nomina", nominaOpt.get());
                return "nomina/detalle-nomina-empleado"; // Nueva plantilla para el empleado
            } else {
                redirectAttributes.addFlashAttribute("error", "Nómina no encontrada o no tiene permiso para verla.");
                return "redirect:/empleados/nominas/mis-nominas";
            }
        } catch (AccessDeniedException e) {
            redirectAttributes.addFlashAttribute("error", "Acceso denegado: " + e.getMessage());
            return "redirect:/usuarios/inicio-sesion";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Recurso no encontrado: " + e.getMessage());
            return "redirect:/empleados/nominas/mis-nominas";
        }
    }

    @GetMapping("/{idNomina}/pdf")
    public ResponseEntity<byte[]> descargarPdfNomina(@PathVariable UUID idNomina,
                                                     HttpSession session,
                                                     RedirectAttributes redirectAttributes) {
        try {
            String emailEmpleado = getEmailAutenticado(session);
            byte[] pdfBytes = nominaEmpleadoService.generarPdfNomina(idNomina, emailEmpleado);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename("nomina_" + idNomina + ".pdf").build());
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (AccessDeniedException e) {
            logger.warn("Intento de descarga de PDF no autorizado para nómina {} por usuario no autenticado o sin permiso.", idNomina);
            // No se puede usar RedirectAttributes aquí, ya que es una respuesta de archivo.
            // El cliente recibirá un error HTTP.
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (EntityNotFoundException e) {
            logger.warn("Intento de descarga de PDF para nómina no existente o no accesible: {}", idNomina);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Error generando PDF para nómina {}: {}", idNomina, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}