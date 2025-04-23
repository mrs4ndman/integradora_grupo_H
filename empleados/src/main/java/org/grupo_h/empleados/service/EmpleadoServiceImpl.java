package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.comun.entity.auxiliar.CuentaCorriente;
import org.grupo_h.comun.entity.auxiliar.Direccion;
import org.grupo_h.empleados.dto.CuentaCorrienteDTO;
import org.grupo_h.empleados.dto.DireccionDTO;
import org.grupo_h.empleados.dto.EmpleadoDetalleDTO;
import org.grupo_h.empleados.dto.EmpleadoRegistroDTO;
import org.grupo_h.empleados.repository.EmpleadoRepository;
import org.grupo_h.empleados.repository.UsuarioRepository;
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

    /**
     * Constructor para inyectar las dependencias necesarias.
     *
     * @param empleadosRepository Repositorio de empleados.
     * @param usuarioRepository   Repositorio de usuarios.
     */
    public EmpleadoServiceImpl(EmpleadoRepository empleadosRepository, UsuarioRepository usuarioRepository) {
        this.empleadosRepository = empleadosRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Registra un nuevo empleado en el sistema.
     *
     * @param empleadoDTO DTO con los datos del empleado a registrar.
     * @return El empleado registrado.
     */
    @Override
    public Empleado registrarEmpleado(EmpleadoRegistroDTO empleadoDTO) {
        DireccionDTO direccionDTO = empleadoDTO.getDireccion();

        Direccion direccion = new Direccion();
        direccion.setTipoVia(direccionDTO.getTipoVia());
        direccion.setVia(direccionDTO.getVia());
        direccion.setNumero(direccionDTO.getNumero());
        direccion.setPiso(direccionDTO.getPiso());
        direccion.setPuerta(direccionDTO.getPuerta());
        direccion.setLocalidad(direccionDTO.getLocalidad());
        direccion.setCodigoPostal(direccionDTO.getCodigoPostal());
        direccion.setRegion(direccionDTO.getRegion());
        direccion.setPais(direccionDTO.getPais());

        CuentaCorrienteDTO cuentaCorrienteDTO = empleadoDTO.getCuentaCorriente();

        CuentaCorriente cuentaCorriente = new CuentaCorriente();
        cuentaCorriente.setBanco(cuentaCorrienteDTO.getBanco());
        cuentaCorriente.setCuentaCorriente(cuentaCorrienteDTO.getCuentaCorriente());

        Empleado empleado = new Empleado();
        empleado.setNombre(empleadoDTO.getNombre());
        empleado.setApellido(empleadoDTO.getApellido());
        empleado.setFechaNacimiento(empleadoDTO.getFechaNacimiento());
        empleado.setEmail(empleadoDTO.getEmail());

        empleado.setDireccion(direccion);
        empleado.setCuentaCorriente(cuentaCorriente);

        byte[] archivoContenido = empleadoDTO.getArchivoContenido();
        String archivoNombreOriginal = empleadoDTO.getArchivoNombreOriginal();

        if (archivoContenido != null && archivoContenido.length > 0) {
            empleado.setArchivoContenido(archivoContenido);
            empleado.setArchivoNombreOriginal(archivoNombreOriginal);
        } else {
            empleado.setArchivoContenido(null);
            empleado.setArchivoNombreOriginal(null);
        }

        return empleadosRepository.save(empleado);
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
            detalleDTO.setApellido(empleado.getApellido());
            detalleDTO.setEmail(empleado.getEmail());
            detalleDTO.setFechaNacimiento(empleado.getFechaNacimiento());
            detalleDTO.setDireccion(empleado.getDireccion());
            detalleDTO.setCuentaCorriente(empleado.getCuentaCorriente());
            detalleDTO.setFechaAltaEnBaseDeDatos(empleado.getFechaAltaEnBaseDeDatos());
            return detalleDTO;
        });
    }
}
