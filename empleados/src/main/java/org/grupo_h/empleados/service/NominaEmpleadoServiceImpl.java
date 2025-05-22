package org.grupo_h.empleados.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.comun.entity.LineaNomina;
import org.grupo_h.comun.entity.Nomina;
import org.grupo_h.comun.entity.Parametros;
import org.grupo_h.comun.repository.EmpleadoRepository;
import org.grupo_h.comun.repository.NominaRepository;
import org.grupo_h.comun.repository.ParametrosRepository;
import org.grupo_h.empleados.dto.*;
import org.grupo_h.empleados.specs.NominaEmpleadoSpecification;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Locale;


@Service
public class NominaEmpleadoServiceImpl implements NominaEmpleadoService {

    private static final Logger logger = LoggerFactory.getLogger(NominaEmpleadoServiceImpl.class);

    private final NominaRepository nominaRepository;
    private final EmpleadoRepository empleadoRepository;
    private final ParametrosRepository parametrosRepository;
    private final ModelMapper modelMapper;

    private static final String PARAM_NOMBRE_EMPRESA = "NOMBRE_EMPRESA";
    private static final String PARAM_CIF_EMPRESA = "CIF_EMPRESA";
    private static final String PARAM_DIRECCION_EMPRESA = "DIRECCION_EMPRESA";

    @Autowired
    public NominaEmpleadoServiceImpl(NominaRepository nominaRepository,
                                     EmpleadoRepository empleadoRepository,
                                     ParametrosRepository parametrosRepository,
                                     ModelMapper modelMapper) {
        this.nominaRepository = nominaRepository;
        this.empleadoRepository = empleadoRepository;
        this.parametrosRepository = parametrosRepository;
        this.modelMapper = modelMapper;
    }

    private Empleado getEmpleadoAutenticado(String emailEmpleado) {
        return empleadoRepository.findByUsuarioEmail(emailEmpleado)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado para el email: " + emailEmpleado));
    }

    private Map<String, String> getEmpresaParametrosMap() {
        Map<String, String> params = new HashMap<>();
        parametrosRepository.findByClave(PARAM_NOMBRE_EMPRESA).ifPresent(p -> params.put(PARAM_NOMBRE_EMPRESA, p.getValor()));
        parametrosRepository.findByClave(PARAM_CIF_EMPRESA).ifPresent(p -> params.put(PARAM_CIF_EMPRESA, p.getValor()));
        parametrosRepository.findByClave(PARAM_DIRECCION_EMPRESA).ifPresent(p -> params.put(PARAM_DIRECCION_EMPRESA, p.getValor()));
        return params;
    }

    private void calcularAcumuladosAnuales(Nomina nomina, NominaEmpleadoDetalleDTO dto) {
        if (nomina.getEmpleado() == null || nomina.getFechaInicio() == null) {
            dto.setCantidadBrutaAcumuladaAnual(nomina.getTotalDevengos());
            dto.setRetencionesAcumuladasAnual(Math.abs(nomina.getTotalDeducciones()));
            dto.setCantidadPercibidaAcumuladaAnual(nomina.getSalarioNeto());
            dto.setAnioEjercicio(nomina.getFechaInicio() != null ? nomina.getFechaInicio().getYear() : LocalDate.now().getYear());
            return;
        }

        int anioActualNomina = nomina.getFechaInicio().getYear();
        List<Nomina> nominasAnteriores = nominaRepository.findNominasAnterioresEnAnioConFechaInicioReferencia(
                nomina.getEmpleado().getId(),
                anioActualNomina,
                nomina.getFechaInicio()
        );

        double brutaAcumuladaPrevia = nominasAnteriores.stream().mapToDouble(Nomina::getTotalDevengos).sum();
        double retencionesAcumuladasPrevia = nominasAnteriores.stream().mapToDouble(n -> Math.abs(n.getTotalDeducciones())).sum();
        double percibidaAcumuladaPrevia = nominasAnteriores.stream().mapToDouble(Nomina::getSalarioNeto).sum();

        dto.setCantidadBrutaAcumuladaAnual(brutaAcumuladaPrevia + nomina.getTotalDevengos());
        dto.setRetencionesAcumuladasAnual(retencionesAcumuladasPrevia + Math.abs(nomina.getTotalDeducciones()));
        dto.setCantidadPercibidaAcumuladaAnual(percibidaAcumuladaPrevia + nomina.getSalarioNeto());
        dto.setAnioEjercicio(anioActualNomina);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<NominaEmpleadoDetalleDTO> obtenerDetalleNominaPorIdParaEmpleado(UUID idNomina, String emailEmpleado) {
        Empleado empleado = getEmpleadoAutenticado(emailEmpleado);
        Optional<Nomina> nominaOpt = nominaRepository.findById(idNomina);

        if (nominaOpt.isEmpty()) {
            logger.warn("Intento de acceso a nómina no existente ID: {} por empleado {}", idNomina, emailEmpleado);
            return Optional.empty();
        }

        Nomina nomina = nominaOpt.get();
        if (!nomina.getEmpleado().getId().equals(empleado.getId())) {
            logger.warn("Acceso denegado: Empleado {} intentando acceder a nómina ID {} que no le pertenece.", emailEmpleado, idNomina);
            throw new AccessDeniedException("No tiene permiso para ver esta nómina.");
        }

        NominaEmpleadoDetalleDTO dto = modelMapper.map(nomina, NominaEmpleadoDetalleDTO.class);
        dto.setLineas(nomina.getLineas().stream()
                .map(linea -> modelMapper.map(linea, LineaNominaDTO.class))
                .collect(Collectors.toList()));
        dto.setTotalDevengos(nomina.getTotalDevengos());
        dto.setTotalDeducciones(nomina.getTotalDeducciones());
        dto.setSalarioNeto(nomina.getSalarioNeto());
        dto.setEmpresaParametros(getEmpresaParametrosMap());
        if (nomina.getEmpleado() != null) {
            dto.setEmpleado(modelMapper.map(nomina.getEmpleado(), EmpleadoSimpleConDNIDTO.class));
        }

        calcularAcumuladosAnuales(nomina, dto);

        return Optional.of(dto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NominaEmpleadoResumenDTO> consultarNominasPorEmpleado(String emailEmpleado,
                                                                      LocalDate fechaDesde,
                                                                      LocalDate fechaHasta,
                                                                      Pageable pageable) {
        Empleado empleado = getEmpleadoAutenticado(emailEmpleado);
        Page<Nomina> nominasPage = nominaRepository.findAll(
                NominaEmpleadoSpecification.conFiltros(empleado.getId(), fechaDesde, fechaHasta),
                pageable
        );

        DateTimeFormatter periodoFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("es", "ES"));

        return nominasPage.map(nomina -> {
            NominaEmpleadoResumenDTO dto = modelMapper.map(nomina, NominaEmpleadoResumenDTO.class);
            dto.setSalarioNeto(nomina.getSalarioNeto());
            dto.setPeriodoLiquidacion(nomina.getFechaInicio().format(periodoFormatter));
            return dto;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generarPdfNomina(UUID idNomina, String emailEmpleado) throws Exception {
        Optional<NominaEmpleadoDetalleDTO> detalleOpt = obtenerDetalleNominaPorIdParaEmpleado(idNomina, emailEmpleado);
        if (detalleOpt.isEmpty()) {
            throw new EntityNotFoundException("Nómina no encontrada o no accesible para el empleado.");
        }
        NominaEmpleadoDetalleDTO nominaDto = detalleOpt.get();

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float margin = 40;
                float yStart = page.getMediaBox().getHeight() - margin;
                float yPosition = yStart;
                float leadingSmall = 1.2f * 8;
                float leadingNormal = 1.2f * 10;
                float leadingHeader = 1.5f * 12;

                PDType1Font fontRegular = PDType1Font.HELVETICA;
                PDType1Font fontBold = PDType1Font.HELVETICA_BOLD;

                // Encabezado Principal
                contentStream.setFont(fontBold, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("RECIBO INDIVIDUAL JUSTIFICATIVO DEL PAGO DE SALARIOS");
                contentStream.endText();
                yPosition -= leadingHeader * 1.5f;

                Map<String, String> empresaParams = nominaDto.getEmpresaParametros();

                // Datos Empresa y Empleado
                float columnWidth = (page.getMediaBox().getWidth() - 2 * margin - 20) / 2;
                float yEmpresa = yPosition;

                // Empresa
                contentStream.setFont(fontBold, 9);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yEmpresa);
                contentStream.showText("EMPRESA");
                contentStream.endText();
                yEmpresa -= leadingNormal;
                contentStream.setFont(fontRegular, 8);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yEmpresa);
                contentStream.showText("Denominación: " + empresaParams.getOrDefault(PARAM_NOMBRE_EMPRESA, "N/D"));
                contentStream.endText();
                yEmpresa -= leadingSmall;
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yEmpresa);
                contentStream.showText("CIF: " + empresaParams.getOrDefault(PARAM_CIF_EMPRESA, "N/D"));
                contentStream.endText();
                yEmpresa -= leadingSmall;
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yEmpresa);
                contentStream.showText("Dirección: " + empresaParams.getOrDefault(PARAM_DIRECCION_EMPRESA, "N/D"));
                contentStream.endText();

                // Empleado
                float xEmpleado = margin + columnWidth + 20;
                float yEmpleado = yPosition;
                EmpleadoSimpleConDNIDTO empDto = nominaDto.getEmpleado();
                contentStream.setFont(fontBold, 9);
                contentStream.beginText();
                contentStream.newLineAtOffset(xEmpleado, yEmpleado);
                contentStream.showText("TRABAJADOR");
                contentStream.endText();
                yEmpleado -= leadingNormal;
                contentStream.setFont(fontRegular, 8);
                contentStream.beginText();
                contentStream.newLineAtOffset(xEmpleado, yEmpleado);
                contentStream.showText("Nombre: " + (empDto != null ? empDto.getNombreCompleto() : "N/D"));
                contentStream.endText();
                yEmpleado -= leadingSmall;
                // Suponiendo que EmpleadoSimpleDTO ahora tiene numeroDocumento o que lo obtienes de otra forma
                contentStream.beginText();
                contentStream.newLineAtOffset(xEmpleado, yEmpleado);
                contentStream.showText("Documento: " + (empDto != null && empDto.getNumeroDocumento() != null ? empDto.getNumeroDocumento() : "N/D"));
                contentStream.endText();
                yEmpleado -= leadingSmall;
                contentStream.beginText();
                contentStream.newLineAtOffset(xEmpleado, yEmpleado);
                contentStream.showText("Nº S.S.: " + (nominaDto.getNumeroSeguridadSocialEmpleado() != null ? nominaDto.getNumeroSeguridadSocialEmpleado() : "N/D"));
                contentStream.endText();
                yEmpleado -= leadingSmall;
                contentStream.beginText();
                contentStream.newLineAtOffset(xEmpleado, yEmpleado);
                contentStream.showText("Puesto: " + (nominaDto.getPuestoEmpleadoNomina() != null ? nominaDto.getPuestoEmpleadoNomina() : "N/D"));
                contentStream.endText();

                yPosition = Math.min(yEmpresa, yEmpleado) - leadingNormal * 1.5f;

                // Período de liquidación
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("es", "ES"));
                contentStream.setFont(fontBold, 9);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("PERÍODO DE LIQUIDACIÓN: DEL " + nominaDto.getFechaInicio().format(dateFormatter) + " AL " + nominaDto.getFechaFin().format(dateFormatter));
                contentStream.endText();
                yPosition -= leadingNormal * 1.5f;

                // Tabla de Devengos y Deducciones
                contentStream.setFont(fontBold, 8);
                float conceptoX = margin + 5;
                float importeX = margin + 300;

                yPosition -= 5;
                contentStream.moveTo(margin, yPosition);
                contentStream.lineTo(page.getMediaBox().getWidth() - margin, yPosition);
                contentStream.stroke();
                yPosition -= leadingSmall;
                contentStream.beginText();
                contentStream.newLineAtOffset(conceptoX, yPosition);
                contentStream.showText("CONCEPTO SALARIAL");
                contentStream.endText();
                contentStream.beginText();
                contentStream.newLineAtOffset(importeX, yPosition);
                contentStream.showText("IMPORTE");
                contentStream.endText();
                yPosition -= 5;
                contentStream.moveTo(margin, yPosition);
                contentStream.lineTo(page.getMediaBox().getWidth() - margin, yPosition);
                contentStream.stroke();
                yPosition -= leadingNormal;

                // I. DEVENGOS
                contentStream.setFont(fontBold, 8);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("I. DEVENGOS");
                contentStream.endText();
                yPosition -= leadingNormal;
                contentStream.setFont(fontRegular, 8);
                for (LineaNominaDTO linea : nominaDto.getLineas()) {
                    if (linea.getCantidad() > 0) {
                        contentStream.beginText();
                        contentStream.newLineAtOffset(conceptoX, yPosition);
                        contentStream.showText(linea.getConcepto());
                        contentStream.endText();
                        contentStream.beginText();
                        contentStream.newLineAtOffset(importeX, yPosition);
                        contentStream.showText(String.format(Locale.GERMAN, "%,.2f", linea.getCantidad()));
                        contentStream.endText();
                        yPosition -= leadingSmall;
                    }
                }
                yPosition -= 5;
                contentStream.setFont(fontBold, 8);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin + 200, yPosition);
                contentStream.showText("A. TOTAL DEVENGOS");
                contentStream.endText();
                contentStream.beginText();
                contentStream.newLineAtOffset(importeX, yPosition);
                contentStream.showText(String.format(Locale.GERMAN, "%,.2f", nominaDto.getTotalDevengos()));
                contentStream.endText();
                yPosition -= leadingNormal * 1.5f;

                // II. DEDUCCIONES
                contentStream.setFont(fontBold, 8);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("II. DEDUCCIONES");
                contentStream.endText();
                yPosition -= leadingNormal;
                contentStream.setFont(fontRegular, 8);
                for (LineaNominaDTO linea : nominaDto.getLineas()) {
                    if (linea.getCantidad() < 0) {
                        contentStream.beginText();
                        contentStream.newLineAtOffset(conceptoX, yPosition);
                        contentStream.showText(linea.getConcepto());
                        contentStream.endText();
                        contentStream.beginText();
                        contentStream.newLineAtOffset(importeX, yPosition);
                        contentStream.showText(String.format(Locale.GERMAN, "%,.2f", Math.abs(linea.getCantidad())));
                        contentStream.endText();
                        yPosition -= leadingSmall;
                    }
                }
                yPosition -= 5;
                contentStream.setFont(fontBold, 8);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin + 200, yPosition);
                contentStream.showText("B. TOTAL A DEDUCIR");
                contentStream.endText();
                contentStream.beginText();
                contentStream.newLineAtOffset(importeX, yPosition);
                contentStream.showText(String.format(Locale.GERMAN, "%,.2f", Math.abs(nominaDto.getTotalDeducciones())));
                contentStream.endText();
                yPosition -= leadingNormal * 2f;

                // LÍQUIDO TOTAL A PERCIBIR
                contentStream.setFont(fontBold, 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin + 125, yPosition);
                contentStream.showText("LÍQUIDO TOTAL A PERCIBIR (A-B)");
                contentStream.endText();
                contentStream.beginText();
                contentStream.newLineAtOffset(importeX, yPosition);
                contentStream.showText(String.format(Locale.GERMAN, "%,.2f €", nominaDto.getSalarioNeto()));
                contentStream.endText();
                yPosition -= leadingHeader * 1.5f;


                // Datos Acumulados Anuales
                if (nominaDto.getCantidadBrutaAcumuladaAnual() != null) {
                    contentStream.setFont(fontBold, 9);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText("RESUMEN ACUMULADO ANUAL (Ejercicio " + nominaDto.getAnioEjercicio() + ")");
                    contentStream.endText();
                    yPosition -= leadingNormal;
                    contentStream.setFont(fontRegular, 8);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText("Total Devengado: " + String.format(Locale.GERMAN, "%,.2f €", nominaDto.getCantidadBrutaAcumuladaAnual()));
                    contentStream.endText();
                    yPosition -= leadingSmall;
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText("Total Deducciones: " + String.format(Locale.GERMAN, "%,.2f €", nominaDto.getRetencionesAcumuladasAnual()));
                    contentStream.endText();
                    yPosition -= leadingSmall;
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText("Total Líquido Percibido: " + String.format(Locale.GERMAN, "%,.2f €", nominaDto.getCantidadPercibidaAcumuladaAnual()));
                    contentStream.endText();
                }
                // Pie (Fecha y Firma)
                yPosition = margin + 30;
                contentStream.setFont(fontRegular, 9);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Fecha de emisión: " + LocalDate.now().format(dateFormatter));
                contentStream.endText();
                contentStream.beginText();
                contentStream.newLineAtOffset(page.getMediaBox().getWidth() - margin - 120, yPosition);
                contentStream.showText("Recibí (Firma del trabajador)");
                contentStream.endText();
                yPosition -= leadingSmall;
                contentStream.moveTo(page.getMediaBox().getWidth() - margin - 150, yPosition);
                contentStream.lineTo(page.getMediaBox().getWidth() - margin - 20, yPosition);
                contentStream.stroke();


            } // Cierra contentStream
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            document.save(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } // Cierra document
    }
}