package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.empleados.dto.EmpleadoDetalleDTO;
import org.grupo_h.empleados.dto.EmpleadoRegistroDTO;
import org.grupo_h.comun.repository.EmpleadoRepository;
import org.grupo_h.comun.repository.GeneroRepository;
import org.grupo_h.comun.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementación del servicio para gestionar operaciones relacionadas con los empleados.
 */
@Service
public class EmpleadoServiceImpl implements EmpleadoService {

    @Autowired
    private final EmpleadoRepository empleadosRepository;
    @Autowired
    private final UsuarioRepository usuarioRepository;
    @Autowired
    private final GeneroRepository generoRepository;

    /**
     * Constructor para inyectar las dependencias necesarias.
     *
     * @param empleadosRepository Repositorio de empleados.
     * @param usuarioRepository   Repositorio de usuarios.
     */
    public EmpleadoServiceImpl(EmpleadoRepository empleadosRepository, UsuarioRepository usuarioRepository, GeneroRepository generoRepository) {
        this.empleadosRepository = empleadosRepository;
        this.usuarioRepository = usuarioRepository;
        this.generoRepository = generoRepository;
    }

    /**
     * Registra un nuevo empleado en el sistema.
     *
     * @param empleadoDTO DTO con los datos del empleado a registrar.
     * @return El empleado registrado.
     */
//    @Override
//    public Empleado registrarEmpleado(EmpleadoRegistroDTO empleadoDTO) {
//        DireccionDTO direccionDTO = empleadoDTO.getDireccion();
//
//        Direccion direccion = new Direccion();
//        direccion.setTipoVia(direccionDTO.getTipoVia());
//        direccion.setVia(direccionDTO.getVia());
//        direccion.setNumero(direccionDTO.getNumero());
//        direccion.setPiso(direccionDTO.getPiso());
//        direccion.setPuerta(direccionDTO.getPuerta());
//        direccion.setLocalidad(direccionDTO.getLocalidad());
//        direccion.setCodigoPostal(direccionDTO.getCodigoPostal());
//        direccion.setRegion(direccionDTO.getRegion());
//        direccion.setPais(direccionDTO.getPais());
//
//        CuentaCorrienteDTO cuentaCorrienteDTO = empleadoDTO.getCuentaCorriente();
//
//        CuentaCorriente cuentaCorriente = new CuentaCorriente();
//        cuentaCorriente.setBanco(cuentaCorrienteDTO.getBanco());
//        cuentaCorriente.setCuentaCorriente(cuentaCorrienteDTO.getCuentaCorriente());
//
//        Empleado empleado = new Empleado();
//        empleado.setNombre(empleadoDTO.getNombre());
//        empleado.setApellidos(empleadoDTO.getApellidos());
//        empleado.setFechaNacimiento(empleadoDTO.getFechaNacimiento());
//        empleado.setEmail(empleadoDTO.getEmail());
//        Genero genero = (Genero) generoRepository.findByCodigoGenero(empleadoDTO.getGenero())
//                .orElseThrow(() -> new IllegalArgumentException("Género no válido"));
//        empleado.setGenero(genero);
//
//        empleado.setDireccion(direccion);
//        empleado.setCuentaCorriente(cuentaCorriente);
//
//        byte[] archivoContenido = empleadoDTO.getFotografia();
//        String archivoNombreOriginal = empleadoDTO.getArchivoNombreOriginal();
//
//        if (archivoContenido != null && archivoContenido.length > 0) {
//            empleado.setFotografia(archivoContenido);
//            empleado.setArchivoNombreOriginal(archivoNombreOriginal);
//        } else {
//            empleado.setFotografia(null);
//            empleado.setArchivoNombreOriginal(null);
//        }
//        // Valida el género si existe
//        Genero genero = (Genero) generoRepository.findByCodigoGenero(empleadoDTO.getGenero())
//                .orElseThrow(() -> new IllegalArgumentException("Invalid gender code"));
//
//        // Mapea de DTO a Entity
//        Empleado empleado = modelMapper.map(empleadoDTO, Empleado.class);
//        empleado.setGenero(genero);
//
//        // Maneja la foto si es necesario
//        if (empleadoDTO.getFotografia() != null && empleadoDTO.getFotografia().length > 0) {
//            empleado.setFotografia(empleadoDTO.getFotografia());
//            empleado.setArchivoNombreOriginal(empleadoDTO.getArchivoNombreOriginal());
//
//
//
//            return empleadosRepository.save(empleado);
//    }

    @Override
    public Empleado registrarEmpleado(EmpleadoRegistroDTO empleadoDTO) {
        return null;
    }

    /**
     * Busca un empleado por su nombre.
     *
     * @param nombreEmpleado Nombre del empleado a buscar.
     * @return Un Optional con el empleado encontrado, si existe.
     */
    @Override
    public Optional<Empleado> findByNombreEmpleado(String nombreEmpleado) {
        return Optional.empty();
    }

    /**
     * Obtiene los detalles de un empleado por su ID.
     *
     * @param id ID del empleado.
     * @return Un Optional con los detalles del empleado, si existe.
     */
    @Override
    public Optional<EmpleadoDetalleDTO> obtenerDetalleEmpleado(UUID id) {
        return empleadosRepository.findById(id).map(empleado -> {
            EmpleadoDetalleDTO detalleDTO = new EmpleadoDetalleDTO();
            detalleDTO.setId(empleado.getId());
            detalleDTO.setNombre(empleado.getNombre());
            detalleDTO.setApellidos(empleado.getApellidos());
            detalleDTO.setEmail(empleado.getEmail());
            detalleDTO.setFechaNacimiento(empleado.getFechaNacimiento());
            detalleDTO.setDireccion(empleado.getDireccion());
            detalleDTO.setCuentaCorriente(empleado.getCuentaCorriente());
            detalleDTO.setFechaAltaEnBaseDeDatos(empleado.getFechaAltaEnBaseDeDatos());
            return detalleDTO;
        });
    }
}
