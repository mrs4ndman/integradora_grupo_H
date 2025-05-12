package org.grupo_h.administracion.controller;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.comun.entity.Nomina;
import org.grupo_h.comun.entity.LineaNomina;
import org.grupo_h.comun.repository.EmpleadoRepository;
import org.grupo_h.comun.repository.LineaNominaRepository;
import org.grupo_h.comun.repository.NominaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/administrador/nominas")
public class NominasController {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private LineaNominaRepository lineaNominaRepository;

    @Autowired
    private NominaRepository nominaRepository;

    // Ya no necesitas LineaNominaRepository aquí, cascada en Nomina se encarga

    @GetMapping("")
    public String redirigirAlDashboard() {
        return "redirect:/administrador/nominas/dashboard";
    }

    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model) {
        return "dashboardNominas";
    }

    @GetMapping("/buscar")
    public String buscarEmpleados(@RequestParam String criterio, Model model) {
        model.addAttribute("empleados",
                empleadoRepository.findByNombreContainingOrApellidosContaining(criterio, criterio));
        return "dashboardNominas";
    }

    @GetMapping("/empleado/{id}/nominas")
    public String listarNominasEmpleado(@PathVariable UUID id, Model model) {
        Empleado empleado = empleadoRepository.findById(id).orElseThrow();
        model.addAttribute("empleado", empleado);
        model.addAttribute("nominas", empleado.getNominas());
        return "empleado-nominas";
    }

    @GetMapping("/detalle/{id}")
    public String mostrarDetalleNomina(@PathVariable UUID id, Model model) {
        Nomina nomina = nominaRepository.findById(id).orElseThrow();
        model.addAttribute("nomina", nomina);
        return "detalle-nomina";
    }

    @GetMapping("/empleado/{id}/crear-nomina")
    public String mostrarFormularioCrearNomina(@PathVariable UUID id, Model model) {
        Empleado empleado = empleadoRepository.findById(id).orElseThrow();
        model.addAttribute("empleado", empleado);
        return "formulario-crear-nomina";
    }

    @PostMapping("/empleado/{id}/crear-nomina")
    @Transactional
    public String crearNomina(@PathVariable UUID id,
                              @RequestParam("fechaInicio") String fechaInicio,
                              @RequestParam("fechaFin") String fechaFin,
                              RedirectAttributes redirectAttributes) {
        try {
            Empleado empleado = empleadoRepository.findById(id).orElseThrow();
            LocalDate inicio = LocalDate.parse(fechaInicio);
            LocalDate fin = LocalDate.parse(fechaFin);

            // Validar fechas
            if (fin.isBefore(inicio)) {
                throw new IllegalArgumentException("Fecha fin debe ser posterior a fecha inicio");
            }

            // Validar solapamiento
            List<Nomina> existing = nominaRepository.findByEmpleadoAndPeriodoSolapado(empleado, inicio, fin);
            if (!existing.isEmpty()) {
                throw new IllegalArgumentException("Período solapado con otra nómina");
            }

            Nomina nomina = new Nomina();
            nomina.setFechaInicio(inicio);
            nomina.setFechaFin(fin);
            nomina.setEmpleado(empleado);
            nominaRepository.save(nomina);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/administrador/nominas/empleado/" + id + "/crear-nomina";
        }
        return "redirect:/administrador/nominas/empleado/" + id + "/nominas";
    }


    @PostMapping("/nomina/{id}/borrar")
    @Transactional
    public String borrarNomina(@PathVariable UUID id) {
        Nomina nomina = nominaRepository.findById(id).orElseThrow();
        UUID empleadoId = nomina.getEmpleado().getId();
        nominaRepository.delete(nomina);
        return "redirect:/administrador/nominas/empleado/" + empleadoId + "/nominas";
    }

    @GetMapping("/nomina/{id}/aniadir-linea")
    public String mostrarFormularioAniadirLinea(@PathVariable UUID id, Model model) {
        Nomina nomina = nominaRepository.findById(id).orElseThrow();
        model.addAttribute("nomina", nomina);
        return "formulario-aniadir-linea";
    }


    @PostMapping("/nomina/{id}/aniadir-linea")
    @Transactional
    public String aniadirLineaNomina(@PathVariable UUID id,
                                     @RequestParam("concepto") String concepto,
                                     @RequestParam(value = "porcentaje", required = false) Double porcentaje,
                                     @RequestParam(value = "cantidad", required = false) Double cantidad,
                                     RedirectAttributes redirectAttributes) {
        try {
            Nomina nomina = nominaRepository.findById(id).orElseThrow();

            // Validar campos
            if ((porcentaje == null && cantidad == null) || (porcentaje != null && cantidad != null)) {
                throw new IllegalArgumentException("Debe especificar porcentaje O cantidad");
            }

            LineaNomina linea = new LineaNomina();
            linea.setConcepto(concepto);
            linea.setPorcentaje(porcentaje);
            linea.setCantidad(cantidad != null ? cantidad : 0.0);

            // Calcular si es porcentaje
            if (porcentaje != null) {
                double salarioBase = nomina.getLineas().stream()
                        .filter(l -> "Salario base".equalsIgnoreCase(l.getConcepto()))
                        .mapToDouble(LineaNomina::getCantidad)
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Falta línea de salario base"));
                linea.setCantidad(salarioBase * porcentaje / 100);
            }

            linea.setNomina(nomina);
            nomina.getLineas().add(linea);

            // Calcular totales
            double total = nomina.getLineas().stream()
                    .mapToDouble(l -> "Salario base".equalsIgnoreCase(l.getConcepto()) ? l.getCantidad() : -l.getCantidad())
                    .sum();

            if (total <= 0) {
                throw new IllegalArgumentException("El salario neto debe ser positivo");
            }

            nominaRepository.save(nomina);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/administrador/nominas/nomina/" + id + "/aniadir-linea";
        }
        return "redirect:/administrador/nominas/detalle/" + id;
    }


    @PostMapping("/linea/{id}/borrar")
    @Transactional
    public String borrarLineaNomina(@PathVariable UUID id) {
        LineaNomina lineaNomina = lineaNominaRepository.findById(id).orElseThrow();
        UUID nominaId = lineaNomina.getNomina().getId();
        lineaNominaRepository.delete(lineaNomina);
        return "redirect:/administrador/nominas/detalle/" + nominaId;
    }

    // Nuevo método para descargar PDF
    @GetMapping("/detalle/{id}/pdf")
    public ResponseEntity<byte[]> generarPDFNomina(@PathVariable UUID id) {
        try {
            Nomina nomina = nominaRepository.findById(id).orElseThrow();
            byte[] pdfBytes = generarPDF(nomina);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename("nomina_" + nomina.getFechaInicio() + ".pdf").build());

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private byte[] generarPDF(Nomina nomina) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Configuración inicial
                float margin = 50;
                float yStart = page.getMediaBox().getHeight() - margin;
                float yPosition = yStart;
                float lineHeight = 14;

                // Fuentes
                PDType1Font fontBold = PDType1Font.HELVETICA_BOLD;
                PDType1Font fontNormal = PDType1Font.HELVETICA;
                float fontSize = 12;

                // Cabecera - Datos de la empresa
                contentStream.setFont(fontBold, 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Nómina de " + nomina.getEmpleado().getNombre() + " " + nomina.getEmpleado().getApellidos());
                contentStream.endText();
                yPosition -= lineHeight * 2;

                // Período
                contentStream.setFont(fontNormal, fontSize);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Período: " + nomina.getFechaInicio() + " - " + nomina.getFechaFin());
                contentStream.endText();
                yPosition -= lineHeight * 1.5f;

                // Tabla de líneas de nómina
                float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
                float[] columnWidths = {200, 100, 100};
                drawTableHeader(contentStream, margin, yPosition, tableWidth, columnWidths, fontBold, fontSize);
                yPosition -= lineHeight * 2;

                // Filas de líneas
                contentStream.setFont(fontNormal, fontSize);
                for (LineaNomina linea : nomina.getLineas()) {
                    drawTableRow(
                            contentStream,
                            margin,
                            yPosition,
                            columnWidths,
                            new String[]{
                                    linea.getConcepto(),
                                    (linea.getPorcentaje() != null) ? linea.getPorcentaje() + "%" : "-",
                                    String.format("%.2f €", linea.getCantidad())
                            },
                            lineHeight
                    );
                    yPosition -= lineHeight;
                }

                // Totales
                yPosition -= lineHeight;
                contentStream.setFont(fontBold, fontSize);
                drawTableRow(
                        contentStream,
                        margin,
                        yPosition,
                        columnWidths,
                        new String[]{"Total Devengos", "", String.format("%.2f €", nomina.getTotalDevengos())},
                        lineHeight
                );
                yPosition -= lineHeight;
                drawTableRow(
                        contentStream,
                        margin,
                        yPosition,
                        columnWidths,
                        new String[]{"Total Deducciones", "", String.format("%.2f €", nomina.getTotalDeducciones())},
                        lineHeight
                );
                yPosition -= lineHeight;
                drawTableRow(
                        contentStream,
                        margin,
                        yPosition,
                        columnWidths,
                        new String[]{"Salario Neto", "", String.format("%.2f €", nomina.getSalarioNeto())},
                        lineHeight
                );

            }

            // Guardar en byte array
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            document.save(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
    }

    // Métodos auxiliares para dibujar tablas
    private void drawTableHeader(PDPageContentStream contentStream, float x, float y, float tableWidth, float[] columnWidths, PDType1Font font, float fontSize) throws IOException {
        contentStream.setFont(font, fontSize);
        contentStream.setLineWidth(1f);

        float nextX = x;
        for (int i = 0; i < columnWidths.length; i++) {
            contentStream.beginText();
            contentStream.newLineAtOffset(nextX + 5, y - 10);
            contentStream.showText(switch (i) {
                case 0 -> "Concepto";
                case 1 -> "Porcentaje";
                case 2 -> "Cantidad";
                default -> "";
            });
            contentStream.endText();
            nextX += columnWidths[i];
        }

        // Línea inferior del encabezado
        contentStream.moveTo(x, y - 15);
        contentStream.lineTo(x + tableWidth, y - 15);
        contentStream.stroke();
    }

    private void drawTableRow(PDPageContentStream contentStream, float x, float y, float[] columnWidths, String[] cells, float lineHeight) throws IOException {
        float nextX = x;
        for (int i = 0; i < cells.length; i++) {
            contentStream.beginText();
            contentStream.newLineAtOffset(nextX + 5, y);
            contentStream.showText(cells[i]);
            contentStream.endText();
            nextX += columnWidths[i];
        }
    }
}
