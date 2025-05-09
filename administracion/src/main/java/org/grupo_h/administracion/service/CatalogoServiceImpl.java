package org.grupo_h.administracion.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.grupo_h.administracion.dto.CatalogoDTO;
import org.grupo_h.administracion.dto.ProductoImportDTO;
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
        CatalogoDTO catalogoDto;
        try {
            catalogoDto = objectMapper.readValue(archivoJson.getInputStream(), CatalogoDTO.class);
        } catch (IOException e) {
            logger.error("Error de sintaxis en el archivo JSON o error de lectura: {}", e.getMessage());
            throw new Exception("Error de sintaxis en el archivo JSON: " + e.getMessage());
        }

        validarDatosCatalogo(catalogoDto); // Ya no valida fechaEnvioCatalogo
        Proveedor proveedor = proveedorRepository.findByNombre(catalogoDto.getProveedor())
                .orElseThrow(() -> new Exception("Proveedor '" + catalogoDto.getProveedor() + "' no encontrado en la BD."));

        AtomicInteger productosInsertados = new AtomicInteger(0);
        AtomicInteger productosActualizados = new AtomicInteger(0);
        List<String> erroresValidacionProductos = new ArrayList<>();
        List<Producto> productosAProcesar = new ArrayList<>();

        for (int i = 0; i < catalogoDto.getProductos().size(); i++) {
            ProductoImportDTO productoDto = catalogoDto.getProductos().get(i);
            String logPrefix = "Producto #" + (i + 1) + " (" + (productoDto.getDescripcion() != null ? productoDto.getDescripcion() : "N/A") + "): ";
            try {
                validarProductoDto(productoDto, i + 1);
                Producto productoEntidad = convertirDtoAEntidad(productoDto, proveedor);
                productosAProcesar.add(productoEntidad);
            } catch (Exception e) {
                erroresValidacionProductos.add(logPrefix + e.getMessage());
            }
        }

        if (!erroresValidacionProductos.isEmpty()) {
            String errorSummary = "Errores de validación en los productos.";
            logger.warn("{}\n{}", errorSummary, String.join("\n", erroresValidacionProductos));
            throw new Exception(errorSummary + "\n" + String.join("\n", erroresValidacionProductos));
        }

        for (Producto entidadAGuardar : productosAProcesar) {
            Optional<Producto> existenteOpt = productoRepository.findByDescripcionAndProveedor(entidadAGuardar.getDescripcion(), proveedor);
            if (existenteOpt.isPresent()) {
                Producto existente = existenteOpt.get();
                existente.setPrecio(entidadAGuardar.getPrecio());
                existente.setUnidades(existente.getUnidades() + entidadAGuardar.getUnidades());
                if (entidadAGuardar.getFechaFabricacion() != null) {
                    existente.setFechaFabricacion(entidadAGuardar.getFechaFabricacion());
                }
                existente.setMarca(entidadAGuardar.getMarca());
                existente.setEsPerecedero(entidadAGuardar.getEsPerecedero());
                if(entidadAGuardar.getCategoria() != null) existente.setCategoria(entidadAGuardar.getCategoria());

                productoRepository.save(existente);
                productosActualizados.incrementAndGet();
            } else {
                productoRepository.save(entidadAGuardar);
                productosInsertados.incrementAndGet();
            }
        }

        return String.format("Importación completada. Productos nuevos: %d. Productos actualizados: %d.",
                productosInsertados.get(), productosActualizados.get());
    }

    private void validarDatosCatalogo(CatalogoDTO catalogoDto) throws Exception {
        if (catalogoDto.getProveedor() == null || catalogoDto.getProveedor().trim().isEmpty()) {
            throw new Exception("El campo 'proveedor' del catálogo es obligatorio.");
        }

        if (catalogoDto.getProductos() == null || catalogoDto.getProductos().isEmpty()) {
            throw new Exception("El catálogo debe contener al menos un producto.");
        }
    }

    // Método actualizado
    private void validarProductoDto(ProductoImportDTO dto, int numProducto) throws Exception {
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

        // Validaciones específicas por tipo (sin cambios aquí)
        if (dto.getTitulo() != null) {
            if (dto.getTitulo().trim().isEmpty()) throw new Exception("Título de libro no puede ser vacío.");
        } else if (dto.getDimensiones() != null) {
            if (dto.getDimensiones().getAncho() == null || dto.getDimensiones().getAncho() <= 0 ||
                    dto.getDimensiones().getProfundo() == null || dto.getDimensiones().getProfundo() <= 0 ||
                    dto.getDimensiones().getAlto() == null || dto.getDimensiones().getAlto() <= 0) {
                throw new Exception("Dimensiones de mueble (ancho, profundo, alto) son obligatorias y deben ser mayores que 0.");
            }
        } else if (dto.getTalla() != null) {
            if (dto.getTalla().trim().isEmpty()) throw new Exception("Talla de ropa no puede ser vacía.");
        }
    }

    private Producto convertirDtoAEntidad(ProductoImportDTO dto, Proveedor proveedor) throws Exception {
        Categoria categoriaPrincipal = null;
        if (dto.getCategorias() != null && !dto.getCategorias().isEmpty()) {
            String nombreCategoriaPrincipal = dto.getCategorias().get(0);
            Optional<Categoria> catOpt = categoriaRepository.findByNombre(nombreCategoriaPrincipal);
            if (catOpt.isPresent()) {
                categoriaPrincipal = catOpt.get();
            } else {
                Categoria nuevaCategoria = new Categoria();
                nuevaCategoria.setNombre(nombreCategoriaPrincipal);
                categoriaPrincipal = categoriaRepository.save(nuevaCategoria);
            }
        } else {
            throw new Exception("El producto debe tener al menos una categoría.");
        }

        LocalDate fechaFabricacion = (dto.getFechaFabricacion() != null && !dto.getFechaFabricacion().trim().isEmpty())
                ? parsearFecha(dto.getFechaFabricacion(), "fechaFabricacion") : null;
        if (fechaFabricacion == null) {
            throw new Exception("La fecha de fabricación es obligatoria.");
        }

        Producto producto;
        if (dto.getTitulo() != null && !dto.getTitulo().isEmpty()) {
            Libro libro = new Libro();
            libro.setTitulo(dto.getTitulo());
            libro.setAutor(dto.getAutor());
            libro.setEditorial(dto.getEditorial());
            libro.setTapa(dto.getTapa());
            libro.setNumeroPaginas(dto.getNumeroPaginas());
            libro.setSegundaMano(dto.getSegundaMano());
            producto = libro;
        } else if (dto.getDimensiones() != null &&
                (dto.getDimensiones().getAncho() != null || dto.getDimensiones().getProfundo() != null || dto.getDimensiones().getAlto() != null) ) {
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
                    ". Asegúrese de que el JSON incluye campos distintivos (titulo, dimensiones, talla) o un campo 'tipoProducto'.");
        }

        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setMarca(dto.getMarca());
        producto.setUnidades(dto.getUnidades());
        producto.setEsPerecedero(dto.getEsPerecedero());
        producto.setFechaFabricacion(fechaFabricacion);
        producto.setProveedor(proveedor);
        producto.setCategoria(categoriaPrincipal);

        return producto;
    }

    private LocalDate parsearFecha(String fechaStr, String nombreCampo) throws Exception {
        if (fechaStr == null || fechaStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(fechaStr); // Asume formato YYYY-MM-DD
        } catch (DateTimeParseException e) {
            throw new Exception("Formato de fecha inválido para '" + nombreCampo + "': '" + fechaStr + "'. Usar YYYY-MM-DD.");
        }
    }
}