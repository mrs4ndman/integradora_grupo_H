package org.grupo_h.empleados.service;

import org.grupo_h.comun.entity.Empleado;
import org.grupo_h.comun.entity.auxiliar.CuentaCorriente;
import org.grupo_h.comun.entity.auxiliar.Direccion;
import org.grupo_h.empleados.dto.CuentaCorrienteDTO;
import org.grupo_h.empleados.dto.DireccionDTO;
import org.grupo_h.empleados.dto.EmpleadoRegistroDTO;
import org.grupo_h.empleados.repository.EmpleadoRepository;
import org.grupo_h.empleados.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmpleadoServiceImpl implements EmpleadoService {

    @Autowired
    private final EmpleadoRepository empleadosRepository;
    @Autowired
    private final UsuarioRepository usuarioRepository;

    public EmpleadoServiceImpl(EmpleadoRepository empleadosRepository, UsuarioRepository usuarioRepository) {
        this.empleadosRepository = empleadosRepository;
        this.usuarioRepository = usuarioRepository;
    }

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

        empleado.setDireccion(direccion); // aquí se establece la dirección embebida
        empleado.setCuentaCorriente(cuentaCorriente); // aquí se establece la Cuenta Corriente embebida

        byte[] archivoContenido = empleadoDTO.getArchivoContenido(); // Obtener bytes
        String archivoNombreOriginal = empleadoDTO.getArchivoNombreOriginal(); // Obtener nombre original

        // Asignar directamente a la entidad si hay contenido
        if (archivoContenido != null && archivoContenido.length > 0) {
            empleado.setArchivoContenido(archivoContenido);
            empleado.setArchivoNombreOriginal(archivoNombreOriginal);
            System.out.println("Servicio: Contenido del archivo [" + archivoNombreOriginal + "] asignado a la entidad Empleado para guardar en BD.");
        } else {
            // Asegurarse de que los campos sean null en la entidad si no hay archivo
            empleado.setArchivoContenido(null);
            empleado.setArchivoNombreOriginal(null);
            System.out.println("Servicio: No había contenido de archivo en el DTO para asignar a la entidad.");
        }

        return empleadosRepository.save(empleado);
    }

    @Override
    public Optional<Empleado> findByNombreEmpleado(String nombreEmpleado) {
        return Optional.empty();
    }


}
