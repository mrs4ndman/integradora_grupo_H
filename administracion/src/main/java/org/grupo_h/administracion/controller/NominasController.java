package org.grupo_h.administracion.controller;

import jakarta.servlet.http.HttpSession;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.grupo_h.administracion.dto.EmpleadoSimpleDTO;
import org.grupo_h.administracion.service.EmpleadoService;
import org.grupo_h.administracion.specs.NominaSpecifications;
import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.comun.entity.Nomina;
import org.grupo_h.comun.entity.LineaNomina;
import org.grupo_h.comun.entity.Parametros;
import org.grupo_h.comun.repository.EmpleadoRepository;
import org.grupo_h.comun.repository.LineaNominaRepository;
import org.grupo_h.comun.repository.NominaRepository;
import org.grupo_h.comun.repository.ParametrosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/administrador/nominas")
public class NominasController {

    private static final String PARAM_NOMBRE_EMPRESA = "NOMBRE_EMPRESA";
    private static final String PARAM_CIF_EMPRESA = "CIF_EMPRESA";
    private static final String PARAM_DIRECCION_EMPRESA = "DIRECCION_EMPRESA";

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private LineaNominaRepository lineaNominaRepository;

    @Autowired
    private NominaRepository nominaRepository;

    @Autowired
    private ParametrosRepository parametrosRepository;

    /**
     * Método auxiliar para obtener los parámetros de la empresa desde la base de datos
     *
     * @return Mapa con los parámetros de la empresa (nombre, CIF, dirección)
     */
    private Map<String, String> getEmpresaParametrosMap() {
        Map<String, String> params = new HashMap<>();
        parametrosRepository.findByClave(PARAM_NOMBRE_EMPRESA).ifPresent(p -> params.put(PARAM_NOMBRE_EMPRESA, p.getValor()));
        parametrosRepository.findByClave(PARAM_CIF_EMPRESA).ifPresent(p -> params.put(PARAM_CIF_EMPRESA, p.getValor()));
        parametrosRepository.findByClave(PARAM_DIRECCION_EMPRESA).ifPresent(p -> params.put(PARAM_DIRECCION_EMPRESA, p.getValor()));
        return params;
    }

    /**
     * Verifica si una nómina puede ser modificada según la fecha de fin
     * Una nómina puede modificarse si su fecha de fin es posterior al final del mes anterior
     *
     * @param nomina La nómina a verificar
     * @return true si la nómina es modificable, false si no lo es
     */
    private boolean esNominaModificable(Nomina nomina) {
        // LocalDate hoy = LocalDate.now();
        LocalDate fechaLimite = YearMonth.now().minusMonths(1).atEndOfMonth();
        return nomina.getFechaFin().isAfter(fechaLimite);
    }

    /**
     * Calcula los acumulados anuales para una nómina (bruto, retenciones y percibido)
     * Si la nómina no tiene empleado o fecha inicio, usa solo los valores actuales
     * Si tiene empleado y fecha, suma los acumulados de las nóminas anteriores del mismo año
     *
     * @param nomina La nómina para la que calcular los acumulados
     */
    private void calcularAcumuladosAnuales(Nomina nomina) {
        if (nomina.getEmpleado() == null || nomina.getFechaInicio() == null) {
            nomina.setCantidadBrutaAcumuladaAnual(nomina.getTotalDevengos());
            nomina.setRetencionesAcumuladasAnual(Math.abs(nomina.getTotalDeducciones()));
            nomina.setCantidadPercibidaAcumuladaAnual(nomina.getSalarioNeto());
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

        nomina.setCantidadBrutaAcumuladaAnual(brutaAcumuladaPrevia + nomina.getTotalDevengos());
        nomina.setRetencionesAcumuladasAnual(retencionesAcumuladasPrevia + Math.abs(nomina.getTotalDeducciones()));
        nomina.setCantidadPercibidaAcumuladaAnual(percibidaAcumuladaPrevia + nomina.getSalarioNeto());

    }

    /**
     * Redirige el acceso a la raíz del controlador hacia la consulta de nóminas
     *
     * @return Redirección a la página de consulta de nóminas
     */
    @GetMapping("")
    public String redirigirAlDashboard() {
        return "redirect:/administrador/nominas/consultar";
    }

    /**
     * Muestra la página de consulta de nóminas con filtros y paginación
     *
     * @param empleadoId     ID del empleado a filtrar
     * @param nombreEmpleado Nombre del empleado a filtrar
     * @param fechaDesde     Fecha inicial para filtrar nóminas
     * @param fechaHasta     Fecha final para filtrar nóminas
     * @param pageable       Información de paginación y ordenación
     * @param model          Modelo para pasar datos a la vista
     * @return Vista de consulta de nóminas
     */
    @GetMapping("/consultar")
    public String consultarNominas(
            @RequestParam(value = "empleadoId", required = false) UUID empleadoId,
            @RequestParam(value = "nombreEmpleado", required = false) String nombreEmpleado,
            @RequestParam(value = "fechaDesde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(value = "fechaHasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @PageableDefault(size = 10, sort = "fechaInicio", direction = Sort.Direction.DESC) Pageable pageable,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        String email = (String) session.getAttribute("emailAutenticadoAdmin");
        if (email == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para ver esta información.");
            return "redirect:/administrador/inicio-sesion";
        }
        model.addAttribute("listaEmpleados", empleadoRepository.findAll(Sort.by("apellidos", "nombre")));
        Page<Nomina> paginaNominas = nominaRepository.findAll(
                NominaSpecifications.conFiltros(empleadoId, nombreEmpleado, fechaDesde, fechaHasta),
                pageable
        );

        model.addAttribute("nominas", paginaNominas.getContent());
        model.addAttribute("paginaNominas", paginaNominas);
        model.addAttribute("empleadoId", empleadoId);
        model.addAttribute("nombreEmpleado", nombreEmpleado);
        model.addAttribute("fechaDesde", fechaDesde);
        model.addAttribute("fechaHasta", fechaHasta);
        model.addAttribute("empresaNombre", parametrosRepository.findByClave(PARAM_NOMBRE_EMPRESA).map(Parametros::getValor).orElse("Empresa (No configurada)"));
        return "nomina/consulta-nominas";
    }

    // En NominasController.java, por ejemplo
    @GetMapping("/seleccionar-empleado-para-nomina")
    public String mostrarSeleccionarEmpleadoParaNomina(
            @RequestParam(name = "terminoBusqueda", required = false) String terminoBusqueda,
            Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("emailAutenticadoAdmin") == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para acceder a esta página.");
            return "redirect:/administrador/inicio-sesion";
        }
        // Suponiendo que tienes un método en EmpleadoService para obtener todos los empleados activos
        // o una forma de listarlos para selección.
        List<EmpleadoSimpleDTO> empleados;
        List<EmpleadoSimpleDTO> todosLosEmpleados = empleadoService.obtenerTodosLosEmpleadosParaSeleccion();

        if (StringUtils.hasText(terminoBusqueda)) {
            String terminoBusquedaLower = terminoBusqueda.toLowerCase();
            empleados = todosLosEmpleados.stream()
                    .filter(emp -> (emp.getNombre() != null && emp.getNombre().toLowerCase().contains(terminoBusquedaLower)) ||
                                    (emp.getApellidos() != null && emp.getApellidos().toLowerCase().contains(terminoBusquedaLower))
                            // Podrías añadir búsqueda por DNI si el EmpleadoSimpleDTO lo tuviera y fuera relevante
                    )
                    .collect(Collectors.toList());
            model.addAttribute("terminoBusqueda", terminoBusqueda);
        } else {
            empleados = todosLosEmpleados;
        }

        // Opcional: ordenar la lista resultante si no viene ordenada del servicio
        empleados.sort(Comparator.comparing(EmpleadoSimpleDTO::getApellidos, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(EmpleadoSimpleDTO::getNombre, String.CASE_INSENSITIVE_ORDER));

        model.addAttribute("listaTodosEmpleados", empleados);
        model.addAttribute("empresaNombre", parametrosRepository.findByClave("NOMBRE_EMPRESA").map(Parametros::getValor).orElse("Empresa"));
        return "nomina/seleccionar-empleado-para-nomina";
    }


    /**
     * Muestra la lista de nóminas asociadas a un empleado específico
     *
     * @param empleadoId ID del empleado cuyas nóminas se quieren consultar
     * @param model      Modelo para pasar datos a la vista
     * @return Vista con la lista de nóminas del empleado
     * @throws IllegalArgumentException si no se encuentra el empleado
     */
    @GetMapping("/empleado/{empleadoId}/nominas")
    public String buscarEmpleados(@PathVariable UUID empleadoId, Model model) {
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado - ID: " + empleadoId));
        model.addAttribute("empleado", empleado);
        model.addAttribute("nominas", nominaRepository.findByEmpleadoId(empleadoId)
                .stream().sorted((n1, n2) -> n2.getFechaInicio().compareTo(n1.getFechaInicio()))
                .collect(Collectors.toList()));
        return "nomina/empleado-nominas-lista";
    }

    /**
     * Muestra los detalles de una nómina específica
     *
     * @param id    ID de la nómina a mostrar
     * @param model Modelo para pasar datos a la vista
     * @return Vista con los detalles de la nómina
     * @throws IllegalArgumentException si no se encuentra la nómina
     */
    @GetMapping("/detalle/{id}")
    public String mostrarDetalleNomina(@PathVariable UUID id, Model model) {
        Nomina nomina = nominaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nomina no encontrada - ID: " + id));
        calcularAcumuladosAnuales(nomina);
        model.addAttribute("nomina", nomina);
        model.addAttribute("empresaParametros", getEmpresaParametrosMap());
        return "nomina/detalle-nomina";
    }

    /**
     * Muestra el formulario para crear una nueva nómina para un empleado
     *
     * @param id    ID del empleado para el que se creará la nómina
     * @param model Modelo para pasar datos a la vista
     * @return Vista con el formulario de creación de nómina
     * @throws IllegalArgumentException si no se encuentra el empleado
     */
    @GetMapping("/empleado/{id}/crear-nomina")
    public String mostrarFormularioCrearNomina(@PathVariable UUID id, Model model) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado - ID: " + id));
        model.addAttribute("empleado", empleado);
        Nomina nuevaNomina = new Nomina();
        nominaRepository.findByEmpleadoId(id).stream()
                .filter(n -> n.getNumeroSeguridadSocialEmpleado() != null && !n.getNumeroSeguridadSocialEmpleado().isEmpty())
                .max((n1, n2) -> n1.getFechaFin().compareTo(n2.getFechaFin()))
                .ifPresent(ultimaNomina -> nuevaNomina.setNumeroSeguridadSocialEmpleado(ultimaNomina.getNumeroSeguridadSocialEmpleado()));
        model.addAttribute("nomina", nuevaNomina);
        return "nomina/formulario-crear-nomina";
    }

    /**
     * Procesa la creación de una nueva nómina para un empleado
     *
     * @param id                            ID del empleado para el que se crea la nómina
     * @param fechaInicio                   Fecha de inicio del período de la nómina
     * @param fechaFin                      Fecha de fin del período de la nómina
     * @param numeroSeguridadSocialEmpleado Número de la Seguridad Social del empleado
     * @param redirectAttributes            Atributos para enviar mensajes en la redirección
     * @return Redirección a la vista de detalle de la nómina creada o al formulario en caso de error
     */
    @PostMapping("/empleado/{id}/crear-nomina")
    @Transactional
    public String crearNomina(@PathVariable UUID id,
                              @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                              @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                              @RequestParam(value = "numeroSeguridadSocialEmpleado", required = false) String numeroSeguridadSocialEmpleado,
                              RedirectAttributes redirectAttributes) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado - ID: " + id));
        try {
            if (fechaInicio == null || fechaFin == null) {
                throw new IllegalArgumentException("Las fechas de inicio y fin son obligatorias.");
            }
            if (fechaFin.isBefore(fechaInicio)) {
                throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio.");
            }

            // Validar que no se cree una nómina con ambas fechas en el pasado
            LocalDate hoy = LocalDate.now();
            if (fechaInicio.isBefore(hoy) && fechaFin.isBefore(hoy)) {
                throw new IllegalArgumentException("No se pueden crear nóminas con ambas fechas (inicio y fin) en el pasado.");
            }

            List<Nomina> nominasSolapadas = nominaRepository.findByEmpleadoAndPeriodoSolapado(empleado, fechaInicio, fechaFin);
            if (!nominasSolapadas.isEmpty()) {
                throw new IllegalArgumentException("El período de la nómina se solapa con una nómina existente para este empleado.");
            }

            Nomina nomina = new Nomina();
            nomina.setFechaInicio(fechaInicio);
            nomina.setFechaFin(fechaFin);
            nomina.setNumeroSeguridadSocialEmpleado(numeroSeguridadSocialEmpleado);
            nomina.setEmpleado(empleado);
            Nomina nominaGuardada = nominaRepository.save(nomina);
            redirectAttributes.addFlashAttribute("success", "Nómina creada correctamente. Añada las líneas de nómina.");
            return "redirect:/administrador/nominas/detalle/" + nominaGuardada.getId();
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear la nómina: " + e.getMessage());
            return "redirect:/administrador/nominas/empleado/" + id + "/crear-nomina";
        }
    }

    // Método para mostrar formulario de edición de nómina
    @GetMapping("/nomina/{id}/editar")
    public String mostrarFormularioEditarNomina(@PathVariable UUID id, Model model) {
        Nomina nomina = nominaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nómina no encontrada - ID: " + id));

        if (!esNominaModificable(nomina)) {
            model.addAttribute("error", "No se puede modificar una nómina ya procesada/cobrada.");
            return "redirect:/administrador/nominas/detalle/" + id;
        }

        model.addAttribute("nomina", nomina);
        model.addAttribute("empresaParametros", getEmpresaParametrosMap());
        return "nomina/formulario-editar-nomina";
    }

    // Método para procesar la edición de nómina
    @PostMapping("/nomina/{id}/editar")
    @Transactional
    public String editarNomina(@PathVariable UUID id,
                               @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                               @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                               RedirectAttributes redirectAttributes) {
        Nomina nomina = nominaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nómina no encontrada - ID: " + id));

        if (!esNominaModificable(nomina)) {
            redirectAttributes.addFlashAttribute("error", "No se puede modificar una nómina ya procesada/cobrada.");
            return "redirect:/administrador/nominas/detalle/" + id;
        }

        try {
            if (fechaInicio == null || fechaFin == null) {
                throw new IllegalArgumentException("Las fechas de inicio y fin son obligatorias.");
            }
            if (fechaFin.isBefore(fechaInicio)) {
                throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio.");
            }

            List<Nomina> nominasSolapadas = nominaRepository.findByEmpleadoAndPeriodoSolapado(
                    nomina.getEmpleado(), fechaInicio, fechaFin);
            nominasSolapadas.removeIf(n -> n.getId().equals(id)); // Excluir la nómina actual

            if (!nominasSolapadas.isEmpty()) {
                throw new IllegalArgumentException("El nuevo período se solapa con otra nómina existente.");
            }

            nomina.setFechaInicio(fechaInicio);
            nomina.setFechaFin(fechaFin);
            nominaRepository.save(nomina);

            redirectAttributes.addFlashAttribute("success", "Nómina modificada correctamente.");
            return "redirect:/administrador/nominas/detalle/" + id;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Error al modificar la nómina: " + e.getMessage());
            return "redirect:/administrador/nominas/nomina/" + id + "/editar";
        }
    }

    /**
     * Muestra el formulario para añadir una línea a una nómina existente
     *
     * @param id    ID de la nómina a la que se añadirá la línea
     * @param model Modelo para pasar datos a la vista
     * @return Vista con el formulario para añadir línea de nómina
     */
    @GetMapping("/nomina/{id}/aniadir-linea")
    public String mostrarFormularioAniadirLinea(@PathVariable UUID id, Model model) {
        Nomina nomina = nominaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nómina no encontrada ID: " + id));
        if (!esNominaModificable(nomina)) {
            model.addAttribute("errorSoloLectura", "Esta nómina no es modificable por ser de un periodo ya cerrado.");
        }
        model.addAttribute("nomina", nomina);
        model.addAttribute("lineaNomina", new LineaNomina());
        return "nomina/formulario-aniadir-linea";
    }


    @PostMapping("/nomina/{id}/aniadir-linea")
    @Transactional
    public String aniadirLineaNomina(
            @PathVariable UUID id,
            @RequestParam("concepto") String concepto,
            @RequestParam(value = "porcentaje", required = false) Double porcentaje,
            @RequestParam(value = "cantidad", required = false) Double cantidad,
            RedirectAttributes redirectAttributes) {

        Nomina nomina = nominaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nómina no encontrada ID: " + id));

        if (!esNominaModificable(nomina)) {
            redirectAttributes.addFlashAttribute("error", "No se pueden añadir líneas a una nómina ya procesada/cobrada.");
            return "redirect:/administrador/nominas/detalle/" + id;
        }

        try {
            // Validaciones
            if (concepto == null || concepto.trim().isEmpty()) {
                throw new IllegalArgumentException("El concepto de la línea no puede estar vacío.");
            }

            boolean porcentajePresente = porcentaje != null;
            boolean cantidadPresente = cantidad != null;

            if (porcentajePresente && cantidadPresente && cantidad != 0.0) {
                throw new IllegalArgumentException("No puede especificar porcentaje y cantidad (distinta de cero) simultáneamente. La cantidad se calculará si se indica porcentaje.");
            }
            if (!porcentajePresente && !cantidadPresente) {
                throw new IllegalArgumentException("Debe especificar un porcentaje o una cantidad.");
            }

            // Crear la entidad LineaNomina
            LineaNomina lineaNomina = new LineaNomina();
            lineaNomina.setConcepto(concepto);
            lineaNomina.setPorcentaje(porcentaje);
            lineaNomina.setCantidad(cantidad);

            if (porcentajePresente) {
                double salarioBase = nomina.getLineas().stream()
                        .filter(l -> "Salario base".equalsIgnoreCase(l.getConcepto()))
                        .mapToDouble(LineaNomina::getCantidad)
                        .findFirst()
                        .orElse(0.0);
                if (salarioBase == 0.0 && !"Salario base".equalsIgnoreCase(concepto)) {
                    throw new IllegalArgumentException("Debe existir una línea de 'Salario base' con cantidad positiva para calcular porcentajes.");
                }
                lineaNomina.setCantidad(salarioBase * (porcentaje / 100.0));
            }

            // Asignar la nómina a la línea
            lineaNomina.setNomina(nomina);
            // Buscar si ya existe una nomina con el nombre "Salario base"
            // if (nomina.getLineas().stream().findFirst())
            // nomina.getLineas().add(lineaNomina);

            // Validar que la nómina tenga un salario base válido
            if (!nomina.tieneSalarioBaseValido() && !nomina.getLineas().isEmpty()) {
                throw new IllegalArgumentException("Toda nómina debe tener una línea de 'Salario base' con importe positivo.");
            }

            // Guardar la nómina y la línea de nómina
            nominaRepository.save(nomina);
            lineaNominaRepository.save(lineaNomina);

            redirectAttributes.addFlashAttribute("success", "Línea de nómina añadida correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Error al añadir línea: " + e.getMessage());
        }

        return "redirect:/administrador/nominas/detalle/" + id;
    }


    /**
     * Elimina una línea de una nómina existente si es modificable
     *
     * @param lineaId            ID de la línea de nómina a borrar
     * @param redirectAttributes Atributos para enviar mensajes en la redirección
     * @return Redirección a la vista de detalle de la nómina actualizada
     */
    @PostMapping("/linea/{lineaId}/borrar")
    @Transactional
    public String borrarLineaNomina(@PathVariable UUID lineaId, RedirectAttributes redirectAttributes) {
        LineaNomina lineaNomina = lineaNominaRepository.findById(lineaId)
                .orElseThrow(() -> new IllegalArgumentException("Línea de nómina no encontrada ID: " + lineaId));
        Nomina nomina = lineaNomina.getNomina();

        if (!esNominaModificable(nomina)) {
            redirectAttributes.addFlashAttribute("error", "No se pueden borrar líneas de una nómina ya procesada/cobrada.");
            return "redirect:/administrador/nominas/detalle/" + nomina.getId();
        }

        if ("Salario base".equalsIgnoreCase(lineaNomina.getConcepto())) {
            // Verificar si hay otras líneas de salario base (poco probable pero posible)
            long countSalarioBase = nomina.getLineas().stream().filter(l -> "Salario base".equalsIgnoreCase(l.getConcepto())).count();
            if (countSalarioBase <= 1) { // Si es la única o la última línea de salario base
                redirectAttributes.addFlashAttribute("error", "La línea de 'Salario base' es obligatoria y no puede ser eliminada si es la única con ese concepto.");
                return "redirect:/administrador/nominas/detalle/" + nomina.getId();
            }
        }

        nomina.getLineas().remove(lineaNomina);
        // lineaNominaRepository.delete(lineaNomina); // Se borra por orphanRemoval al guardar nomina
        nominaRepository.save(nomina);

        redirectAttributes.addFlashAttribute("success", "Línea de nómina borrada.");
        return "redirect:/administrador/nominas/detalle/" + nomina.getId();
    }

    /**
     * Genera un PDF con los detalles de una nómina específica
     *
     * @param id ID de la nómina para la que se generará el PDF
     * @return ResponseEntity con el archivo PDF generado o error si falla la generación
     */
    @GetMapping("/detalle/{id}/pdf")
    public ResponseEntity<byte[]> generarPDFNomina(@PathVariable UUID id) {
        Nomina nomina = nominaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nómina no encontrada - ID: " + id));
        calcularAcumuladosAnuales(nomina);
        Map<String, String> empresaParams = getEmpresaParametrosMap();

        try {
            byte[] pdfBytes = generarPDF(nomina, empresaParams);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename("nomina_" + nomina.getEmpleado().getApellidos() + "_" + nomina.getFechaInicio() + ".pdf").build());
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            // Log error e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Método privado que genera el documento PDF de una nómina
     *
     * @param nomina        La nómina para la que se generará el PDF
     * @param empresaParams Mapa con los parámetros de la empresa (nombre, CIF, dirección)
     * @return Array de bytes con el contenido del PDF generado
     * @throws IOException Si ocurre un error en la generación del PDF
     */
    private byte[] generarPDF(Nomina nomina, Map<String, String> empresaParams) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float margin = 40;
                float yStart = page.getMediaBox().getHeight() - margin;
                float yPosition = yStart;
                float leadingSmall = 1.2f * 8; // Espaciado para texto pequeño
                float leadingNormal = 1.2f * 10; // Espaciado para texto normal
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
                Empleado emp = nomina.getEmpleado();
                contentStream.setFont(fontBold, 9);
                contentStream.beginText();
                contentStream.newLineAtOffset(xEmpleado, yEmpleado);
                contentStream.showText("TRABAJADOR");
                contentStream.endText();
                yEmpleado -= leadingNormal;
                contentStream.setFont(fontRegular, 8);
                contentStream.beginText();
                contentStream.newLineAtOffset(xEmpleado, yEmpleado);
                contentStream.showText("Nombre: " + emp.getNombreCompleto());
                contentStream.endText();
                yEmpleado -= leadingSmall;
                contentStream.beginText();
                contentStream.newLineAtOffset(xEmpleado, yEmpleado);
                contentStream.showText(emp.getTipoDocumento().getTipoDocumento() + ": " + emp.getNumeroDocumento());
                contentStream.endText();
                yEmpleado -= leadingSmall;
                contentStream.beginText();
                contentStream.newLineAtOffset(xEmpleado, yEmpleado);
                contentStream.showText("Nº S.S.: " + (nomina.getNumeroSeguridadSocialEmpleado() != null ? nomina.getNumeroSeguridadSocialEmpleado() : "N/D"));
                contentStream.endText();
                yEmpleado -= leadingSmall;
                contentStream.beginText();
                contentStream.newLineAtOffset(xEmpleado, yEmpleado);
                contentStream.showText("Fecha Alta: " + (emp.getFechaAltaEnBaseDeDatos() != null ? emp.getFechaAltaEnBaseDeDatos().toString() : "N/D"));
                contentStream.endText();
                yEmpleado -= leadingSmall;
                contentStream.beginText();
                contentStream.newLineAtOffset(xEmpleado, yEmpleado);
                contentStream.showText("Puesto: " + (nomina.getPuestoEmpleadoNomina() != null ? nomina.getPuestoEmpleadoNomina() : "N/D"));
                contentStream.endText();
                yEmpleado -= leadingSmall;
                contentStream.beginText();
                contentStream.newLineAtOffset(xEmpleado, yEmpleado);
                contentStream.showText("Departamento: " + (emp.getDepartamento() != null ? emp.getDepartamento().toStringBonito() : "N/D"));
                contentStream.endText();

                yPosition = Math.min(yEmpresa, yEmpleado) - leadingNormal * 1.5f;

                // Período de liquidación
                contentStream.setFont(fontBold, 9);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("PERÍODO DE LIQUIDACIÓN: DEL " + nomina.getFechaInicio().toString() + " AL " + nomina.getFechaFin().toString());
                contentStream.endText();
                yPosition -= leadingNormal * 1.5f;

                // Tabla de Devengos y Deducciones
                // Encabezados
                contentStream.setFont(fontBold, 8);
                float conceptoX = margin + 5; // Ajuste para padding
                float importeX = margin + 300; // Ajuste para padding
                float totalX = margin + 450;   // Ajuste para padding

                yPosition -= 5; // Espacio
                // Línea horizontal
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
                yPosition -= 5; // Espacio
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
                for (LineaNomina linea : nomina.getLineas()) {
                    if (linea.getCantidad() > 0) {
                        contentStream.beginText();
                        contentStream.newLineAtOffset(conceptoX, yPosition);
                        contentStream.showText(linea.getConcepto());
                        contentStream.endText();
                        contentStream.beginText();
                        contentStream.newLineAtOffset(importeX, yPosition);
                        contentStream.showText(String.format("%,.2f", linea.getCantidad()));
                        contentStream.endText();
                        yPosition -= leadingSmall;
                    }
                }
                yPosition -= 5; // Espacio
                contentStream.setFont(fontBold, 8);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin + 200, yPosition);
                contentStream.showText("A. TOTAL DEVENGOS");
                contentStream.endText();
                contentStream.beginText();
                contentStream.newLineAtOffset(importeX, yPosition);
                contentStream.showText(String.format("%,.2f", nomina.getTotalDevengos()));
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
                for (LineaNomina linea : nomina.getLineas()) {
                    if (linea.getCantidad() < 0) {
                        contentStream.beginText();
                        contentStream.newLineAtOffset(conceptoX, yPosition);
                        contentStream.showText(linea.getConcepto());
                        contentStream.endText();
                        contentStream.beginText();
                        contentStream.newLineAtOffset(importeX, yPosition);
                        contentStream.showText(String.format("%,.2f", Math.abs(linea.getCantidad())));
                        contentStream.endText();
                        yPosition -= leadingSmall;
                    }
                }
                yPosition -= 5; // Espacio
                contentStream.setFont(fontBold, 8);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin + 200, yPosition);
                contentStream.showText("B. TOTAL A DEDUCIR");
                contentStream.endText();
                contentStream.beginText();
                contentStream.newLineAtOffset(importeX, yPosition);
                contentStream.showText(String.format("%,.2f", Math.abs(nomina.getTotalDeducciones())));
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
                contentStream.showText(String.format("%,.2f €", nomina.getSalarioNeto()));
                contentStream.endText();
                yPosition -= leadingHeader * 1.5f;


                // Datos Acumulados Anuales
                if (nomina.getCantidadBrutaAcumuladaAnual() != null) {
                    contentStream.setFont(fontBold, 9);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText("RESUMEN ACUMULADO ANUAL (Ejercicio " + nomina.getFechaInicio().getYear() + ")");
                    contentStream.endText();
                    yPosition -= leadingNormal;
                    contentStream.setFont(fontRegular, 8);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText("Total Devengado: " + String.format("%,.2f €", nomina.getCantidadBrutaAcumuladaAnual()));
                    contentStream.endText();
                    yPosition -= leadingSmall;
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText("Total Deducciones: " + String.format("%,.2f €", nomina.getRetencionesAcumuladasAnual()));
                    contentStream.endText();
                    yPosition -= leadingSmall;
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText("Total Líquido Percibido: " + String.format("%,.2f €", nomina.getCantidadPercibidaAcumuladaAnual()));
                    contentStream.endText();
                    yPosition -= leadingSmall;
                }

                // Pie (Fecha y Firma)
                yPosition = margin + 30; // Más abajo para la firma
                contentStream.setFont(fontRegular, 9);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Fecha de emisión: " + LocalDate.now().toString());
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
