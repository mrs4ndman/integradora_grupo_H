// src/main/java/org/grupo_h/administracion/service/CatalogoServiceImpl.java
package org.grupo_h.administracion.service;

import com.fasterxml.jackson.core.type.TypeReference; // Necesario para listas
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.grupo_h.administracion.dto.CatalogoDTO;
import org.grupo_h.administracion.dto.ProductoImportDTO;
// Importa tu excepción personalizada si la tienes
import org.grupo_h.administracion.exception.CatalogoImportValidationException;
import org.grupo_h.comun.entity.*;
import org.grupo_h.comun.entity.auxiliar.Dimensiones;
import org.grupo_h.comun.repository.CategoriaRepository;
import org.grupo_h.comun.repository.ProductoRepository;
import org.grupo_h.comun.repository.ProveedorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CatalogoServiceImpl implements CatalogoService {

    private static final Logger logger = LoggerFactory.getLogger(CatalogoServiceImpl.class);

    private final ObjectMapper objectMapper;
    private final ProveedorRepository proveedorRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    @Autowired
    public CatalogoServiceImpl(ProveedorRepository proveedorRepository,
                               CategoriaRepository categoriaRepository,
                               ProductoRepository productoRepository) {
        this.proveedorRepository = proveedorRepository;
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String importarCatalogo(MultipartFile archivoJson) throws Exception {
        List<CatalogoDTO> listaCatalogosDto;
        try {
            // Deserializar el JSON como una LISTA de CatalogoDTO
            listaCatalogosDto = objectMapper.readValue(archivoJson.getInputStream(), new TypeReference<List<CatalogoDTO>>() {});
        } catch (IOException e) {
            logger.error("Error de sintaxis en el archivo JSON o error de lectura: {}", e.getMessage());
            throw new Exception("Error de sintaxis en el archivo JSON (esperaba una lista de catálogos): " + e.getMessage());
        }

        if (listaCatalogosDto == null || listaCatalogosDto.isEmpty()) {
            throw new Exception("El archivo JSON está vacío o no contiene catálogos válidos.");
        }

        AtomicInteger totalProductosInsertados = new AtomicInteger(0);
        AtomicInteger totalProductosActualizados = new AtomicInteger(0);
        List<String> resumenResultadosPorProveedor = new ArrayList<>();
        List<String> erroresGlobalesValidacion = new ArrayList<>();

        // Iterar sobre cada catálogo de proveedor en la lista
        for (int catIdx = 0; catIdx < listaCatalogosDto.size(); catIdx++) {
            CatalogoDTO catalogoDto = listaCatalogosDto.get(catIdx);
            String proveedorNombre = catalogoDto.getProveedor();
            String logPrefixCatalogo = "Catálogo #" + (catIdx + 1) + " (Proveedor: " + (proveedorNombre != null ? proveedorNombre : "N/A") + "): ";

            try {
                validarDatosCatalogo(catalogoDto); // Valida cada catálogo individualmente
                Proveedor proveedor = proveedorRepository.findByNombre(proveedorNombre)
                        .orElseThrow(() -> new Exception(logPrefixCatalogo + "Proveedor '" + proveedorNombre + "' no encontrado en la BD."));

                AtomicInteger productosInsertadosProveedor = new AtomicInteger(0);
                AtomicInteger productosActualizadosProveedor = new AtomicInteger(0);
                List<String> erroresValidacionProductosProveedor = new ArrayList<>();
                List<Producto> productosAProcesarProveedor = new ArrayList<>();

                if (catalogoDto.getProductos() == null || catalogoDto.getProductos().isEmpty()) {
                    logger.warn(logPrefixCatalogo + "No hay productos para procesar para este proveedor.");
                    resumenResultadosPorProveedor.add(logPrefixCatalogo + "Sin productos para procesar.");
                    continue; // Saltar al siguiente catálogo si no hay productos
                }

                for (int i = 0; i < catalogoDto.getProductos().size(); i++) {
                    ProductoImportDTO productoDto = catalogoDto.getProductos().get(i);
                    String logPrefixProducto = logPrefixCatalogo + "Producto #" + (i + 1) + " (" + (productoDto.getDescripcion() != null ? productoDto.getDescripcion() : "N/A") + "): ";
                    try {
                        validarProductoDto(productoDto, i + 1);
                        Producto productoEntidad = convertirDtoAEntidad(productoDto, proveedor);
                        productosAProcesarProveedor.add(productoEntidad);
                    } catch (Exception e) {
                        erroresValidacionProductosProveedor.add(logPrefixProducto + e.getMessage());
                    }
                }

                if (!erroresValidacionProductosProveedor.isEmpty()) {
                    erroresGlobalesValidacion.addAll(erroresValidacionProductosProveedor);
                }

                for (Producto entidadAGuardar : productosAProcesarProveedor) {
                    Optional<Producto> existenteOpt = productoRepository.findByDescripcionAndProveedor(entidadAGuardar.getDescripcion(), proveedor);
                    if (existenteOpt.isPresent()) {
                        Producto existente = existenteOpt.get();
                        // Lógica de actualización (igual que antes)
                        existente.setPrecio(entidadAGuardar.getPrecio());
                        existente.setUnidades(existente.getUnidades() + entidadAGuardar.getUnidades()); // Sumar unidades
                        if (entidadAGuardar.getFechaFabricacion() != null) {
                            existente.setFechaFabricacion(entidadAGuardar.getFechaFabricacion());
                        }
                        existente.setMarca(entidadAGuardar.getMarca());
                        existente.setEsPerecedero(entidadAGuardar.getEsPerecedero());
                        if (entidadAGuardar.getCategorias() != null && !entidadAGuardar.getCategorias().isEmpty()) {
                            existente.setCategorias(new ArrayList<>(entidadAGuardar.getCategorias())); // Reemplaza las categorías
                        } else {
                            existente.setCategorias(new ArrayList<>()); // Limpia las categorías si no hay nuevas
                        }
                        if (existente instanceof Libro && entidadAGuardar instanceof Libro) {
                            Libro libroExistente = (Libro) existente;
                            Libro libroNuevo = (Libro) entidadAGuardar;
                            libroExistente.setTitulo(libroNuevo.getTitulo());
                            libroExistente.setAutor(libroNuevo.getAutor());
                            libroExistente.setEditorial(libroNuevo.getEditorial());
                            libroExistente.setTapa(libroNuevo.getTapa());
                            libroExistente.setNumeroPaginas(libroNuevo.getNumeroPaginas());
                            libroExistente.setSegundaMano(libroNuevo.getSegundaMano());
                        } else if (existente instanceof Mueble && entidadAGuardar instanceof Mueble) {
                            Mueble muebleExistente = (Mueble) existente;
                            Mueble muebleNuevo = (Mueble) entidadAGuardar;
                            muebleExistente.setDimensiones(muebleNuevo.getDimensiones());
                            muebleExistente.setColores(muebleNuevo.getColores());
                            muebleExistente.setPeso(muebleNuevo.getPeso());
                        } else if (existente instanceof Ropa && entidadAGuardar instanceof Ropa) {
                            Ropa ropaExistente = (Ropa) existente;
                            Ropa ropaNueva = (Ropa) entidadAGuardar;
                            ropaExistente.setTalla(ropaNueva.getTalla());
                            ropaExistente.setColoresDisponibles(ropaNueva.getColoresDisponibles());
                        }

                        productoRepository.save(existente);
                        productosActualizadosProveedor.incrementAndGet();
                        totalProductosActualizados.incrementAndGet();
                    } else {
                        productoRepository.save(entidadAGuardar);
                        productosInsertadosProveedor.incrementAndGet();
                        totalProductosInsertados.incrementAndGet();
                    }
                }
                resumenResultadosPorProveedor.add(String.format("Proveedor '%s': %d nuevos, %d actualizados.",
                        proveedorNombre, productosInsertadosProveedor.get(), productosActualizadosProveedor.get()));

            } catch (Exception e) { // Errores a nivel de procesamiento de un catálogo de proveedor (ej. proveedor no encontrado)
                erroresGlobalesValidacion.add(logPrefixCatalogo + e.getMessage());
            }
        }

        if (!erroresGlobalesValidacion.isEmpty()) {
            String errorSummary = "Errores de validación durante la importación de uno o más catálogos.";
            logger.warn("{}\n{}", errorSummary, String.join("\n", erroresGlobalesValidacion));
            throw new CatalogoImportValidationException(errorSummary, erroresGlobalesValidacion);
        }

        return String.format("Importación global completada. Total productos nuevos: %d. Total productos actualizados: %d. Detalles por proveedor:\n%s",
                totalProductosInsertados.get(), totalProductosActualizados.get(), String.join("\n", resumenResultadosPorProveedor));
    }
    private void validarDatosCatalogo(CatalogoDTO catalogoDto) throws Exception {
        if (catalogoDto.getProveedor() == null || catalogoDto.getProveedor().trim().isEmpty()) {
            throw new Exception("El campo 'proveedor' del catálogo es obligatorio.");
        }
    }

    private void validarProductoDto(ProductoImportDTO dto, int numProducto) throws Exception {
        // Validaciones comunes
        if (dto.getDescripcion() == null || dto.getDescripcion().trim().isEmpty()) {
            throw new Exception("Descripción es obligatoria.");
        }
        if (dto.getPrecio() == null || dto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("Precio debe ser mayor que 0.0. Valor recibido: " + dto.getPrecio());
        }
        if (dto.getCategorias() == null || dto.getCategorias().isEmpty()) {
            throw new Exception("Debe tener al menos una categoría.");
        }
        if (dto.getUnidades() == null || dto.getUnidades() <= 0) {
            throw new Exception("Unidades deben ser un entero mayor que 0. Valor recibido: " + dto.getUnidades());
        }

        // Validación fechaFabricacion
        if (dto.getFechaFabricacion() != null && !dto.getFechaFabricacion().trim().isEmpty()) {
            LocalDate fechaFab = parsearFecha(dto.getFechaFabricacion(), "fechaFabricacion para producto " + numProducto);
            if (fechaFab.isAfter(LocalDate.now())) {
                throw new Exception("Fecha de fabricación (" + fechaFab + ") no puede ser futura.");
            }
        } else {
            throw new Exception("Fecha de fabricación es obligatoria y debe ser una fecha pasada o presente.");
        }

        // Validaciones específicas por tipo (inferido por campos presentes)
        boolean esLibro = dto.getTitulo() != null && !dto.getTitulo().isEmpty();
        boolean esMueble = dto.getDimensiones() != null &&
                (dto.getDimensiones().getAncho() != null || dto.getDimensiones().getProfundo() != null || dto.getDimensiones().getAlto() != null);
        boolean esRopa = dto.getTalla() != null && !dto.getTalla().isEmpty();

        if (esLibro) {
            if (dto.getAutor() == null || dto.getAutor().trim().isEmpty()) throw new Exception("Autor del libro es obligatorio.");
            if (dto.getEditorial() == null || dto.getEditorial().trim().isEmpty()) throw new Exception("Editorial del libro es obligatoria.");
            if (dto.getNumeroPaginas() == null || dto.getNumeroPaginas() <=0) throw new Exception("Número de páginas del libro debe ser mayor a 0.");
        } else if (esMueble) {
            if (dto.getDimensiones().getAncho() == null || dto.getDimensiones().getAncho() <= 0 ||
                    dto.getDimensiones().getProfundo() == null || dto.getDimensiones().getProfundo() <= 0 ||
                    dto.getDimensiones().getAlto() == null || dto.getDimensiones().getAlto() <= 0) {
                throw new Exception("Dimensiones de mueble (ancho, profundo, alto) son obligatorias y deben ser mayores que 0.");
            }
        } else if (esRopa) {
        } else {
        }
    }

    private Producto convertirDtoAEntidad(ProductoImportDTO dto, Proveedor proveedor) throws Exception {
        List<Categoria> listaCategoriasDelProducto = new ArrayList<>();
        if (dto.getCategorias() == null || dto.getCategorias().isEmpty()) { // Usando nombresCategorias de ProductoImportDTO
            throw new Exception("El producto '" + dto.getDescripcion() + "' debe tener al menos una categoría especificada en 'nombresCategorias'.");
        }

        for (String nombreCategoria : dto.getCategorias()) {
            if (nombreCategoria == null || nombreCategoria.trim().isEmpty()) continue; // Saltar nombres vacíos si los hubiera

            Optional<Categoria> catOpt = categoriaRepository.findByNombre(nombreCategoria.trim());
            Categoria categoria;
            if (catOpt.isPresent()) {
                categoria = catOpt.get();
            } else {
                logger.info("Creando nueva categoría: {}", nombreCategoria.trim());
                Categoria nuevaCategoria = new Categoria();
                nuevaCategoria.setNombre(nombreCategoria.trim());
                categoria = categoriaRepository.save(nuevaCategoria);
            }
            listaCategoriasDelProducto.add(categoria);
        }

        LocalDate fechaFabricacion = (dto.getFechaFabricacion() != null && !dto.getFechaFabricacion().trim().isEmpty())
                ? parsearFecha(dto.getFechaFabricacion(), "fechaFabricacion") : null;
        if (fechaFabricacion == null) {
            throw new Exception("La fecha de fabricación es obligatoria para el producto '" + dto.getDescripcion() + "'.");
        }

        Producto producto;
        // Inferencia de tipo basada en campos distintivos
        if (dto.getTitulo() != null && !dto.getTitulo().isEmpty()) {
            Libro libro = new Libro();
            libro.setTitulo(dto.getTitulo());
            libro.setAutor(dto.getAutor());
            libro.setEditorial(dto.getEditorial());
            libro.setTapa(dto.getTapa());
            libro.setNumeroPaginas(dto.getNumeroPaginas());
            libro.setSegundaMano(dto.getSegundaMano() != null ? dto.getSegundaMano() : false);
            producto = libro;
        } else if (dto.getDimensiones() != null &&
                (dto.getDimensiones().getAncho() != null || dto.getDimensiones().getProfundo() != null || dto.getDimensiones().getAlto() != null)) {
            Mueble mueble = new Mueble();
            if (dto.getDimensiones() != null) {
                mueble.setDimensiones(new Dimensiones(dto.getDimensiones().getAncho(), dto.getDimensiones().getProfundo(), dto.getDimensiones().getAlto()));
            }
            mueble.setColores(dto.getColoresMueble());
            mueble.setPeso(dto.getPeso());
            producto = mueble;
        } else if (dto.getTalla() != null && !dto.getTalla().isEmpty()) {
            Ropa ropa = new Ropa();
            ropa.setTalla(dto.getTalla());
            ropa.setColoresDisponibles(dto.getColoresRopa());
            producto = ropa;
        } else {
            throw new Exception("No se pudo determinar el tipo de producto para: " + dto.getDescripcion() +
                    ". Asegúrese de que el JSON incluye campos distintivos (titulo, dimensiones, talla).");
        }

        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setMarca(dto.getMarca());
        producto.setUnidades(dto.getUnidades());
        producto.setEsPerecedero(dto.getEsPerecedero() != null ? dto.getEsPerecedero() : false);
        producto.setFechaFabricacion(fechaFabricacion);
        producto.setProveedor(proveedor);
        producto.setCategorias(listaCategoriasDelProducto);

        return producto;
    }

    private LocalDate parsearFecha(String fechaStr, String nombreCampo) throws Exception {
        if (fechaStr == null || fechaStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(fechaStr); // Asume formato yyyy-MM-dd
        } catch (DateTimeParseException e) {
            throw new Exception("Formato de fecha inválido para '" + nombreCampo + "': '" + fechaStr + "'. Usar yyyy-MM-dd.");
        }
    }
}
