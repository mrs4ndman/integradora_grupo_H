package org.grupo_h.comun.StarterEntities;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.grupo_h.comun.entity.*;
import org.grupo_h.comun.entity.auxiliar.*;
import org.grupo_h.comun.repository.*;
import jakarta.transaction.Transactional;
import org.grupo_h.comun.entity.Departamento;
import org.grupo_h.comun.entity.EspecialidadesEmpleado;
import org.grupo_h.comun.entity.auxiliar.*;
import org.grupo_h.comun.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
//  import org.springframework.boot.CommandLineRunner;
//  org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@Component
public class StarterEntities {

    @Autowired
    private GeneroRepository generoRepository;

    @Autowired
    private PaisRepository paisRepository;

    @Autowired
    private TipoViaRepository tipoViaRepository;

    @Autowired
    private ParametrosRepository parametrosRepository;

    @Autowired
    private TipoDocumentoRepository tipoDocumentoRepository;

//    @Autowired
//    private CuentaCorrienteRepository cuentaCorrienteRepository;

    @Autowired
    private EntidadBancariaRepository entidadBancariaRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private EspecialidadesEmpleadoRepository especialidadesEmpleadoRepository;

    @Autowired
    private TipoTarjetaRepository tipoTarjetaRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostConstruct
    public void initGenero() {
        if (generoRepository.count() == 0) {
            generoRepository.save(new Genero("M", "Masculino"));
            generoRepository.save(new Genero("F", "Femenino"));
            generoRepository.save(new Genero("O", "Otro"));
        }
    }

    @PostConstruct
    public void initPais() {
        if (paisRepository.count() == 0) {
            paisRepository.save(new Pais("ES", "España", "+34"));
            paisRepository.save(new Pais("FR", "Francia", "+33"));
            paisRepository.save(new Pais("US", "Estados Unidos", "+1"));
            paisRepository.save(new Pais("IT", "Italia", "+39"));
            paisRepository.save(new Pais("AN", "Andorra", "+376"));
        }
    }

    @PostConstruct
    public void initTipoVia() {
        if (tipoViaRepository.count() == 0) {
            TipoVia calle = new TipoVia();
            calle.setTipoVia("Calle");

            TipoVia avenida = new TipoVia();
            avenida.setTipoVia("Avenida");

            TipoVia paseo = new TipoVia();
            paseo.setTipoVia("Paseo");

            tipoViaRepository.saveAll(List.of(calle, avenida, paseo));
        }
    }

    @PostConstruct
    public void initTipoDocumento() {
        if (tipoDocumentoRepository.count() == 0) {
            TipoDocumento dni = new TipoDocumento();
            dni.setTipoDocumento("DNI");

            TipoDocumento nie = new TipoDocumento();
            nie.setTipoDocumento("NIE");

            tipoDocumentoRepository.saveAll(List.of(dni,nie));

        }
    }


    @PostConstruct
    public void initEntidadBancaria() {
        if (entidadBancariaRepository.count() == 0) {
            try {

              EntidadBancaria santander = new EntidadBancaria();
              santander.setNombreEntidad("Santander");

              EntidadBancaria bbva = new EntidadBancaria();
              bbva.setNombreEntidad("BBVA");

              EntidadBancaria ing = new EntidadBancaria();
              ing.setNombreEntidad("ING");

              entidadBancariaRepository.saveAll(List.of(santander, bbva, ing));


                System.out.println("Datos iniciales de EntidadBancaria cargados exitosamente");
            } catch (Exception e) {
                System.err.println("Error al cargar datos iniciales: " + e.getMessage());
            }
        }
    }

    @PostConstruct
    public void initParametros() {
        if (parametrosRepository.count() == 0) {
            parametrosRepository.save(new Parametros("MAX_INTENTOS_FALLIDOS", "3"));
            parametrosRepository.save(new Parametros("DURACION_BLOQUEO", "15"));
            parametrosRepository.save(new Parametros("DURACION_BLOQUEO_ADMIN", "1440"));
        }
    }

    @PostConstruct
    public void initDepartamento() {
        if (departamentoRepository.count() == 0) {
            departamentoRepository.save(new Departamento("PREP", "Preparación de Pedidos"));
            departamentoRepository.save(new Departamento("ALMAC", "Almacenaje"));
            departamentoRepository.save(new Departamento("RECEP", "Recepción de Mercancías"));
            departamentoRepository.save(new Departamento("VENTA", "Ventas"));
            departamentoRepository.save(new Departamento("ADMIN", "Administración"));
        }

    }

    @Transactional
    @PostConstruct
    public void initEspecialidadEmpleado() {
        if (especialidadesEmpleadoRepository.count() == 0) {

            EspecialidadesEmpleado e1 = new EspecialidadesEmpleado();
            e1.setEspecialidad("Especialista en Gestión de Inventarios");

            EspecialidadesEmpleado e2 = new EspecialidadesEmpleado();
            e2.setEspecialidad("Especialista en IT");

            EspecialidadesEmpleado e3 = new EspecialidadesEmpleado();
            e3.setEspecialidad("Especialista en Logística");

            especialidadesEmpleadoRepository.saveAll(List.of(e1,e2, e3));
        }
    }

    @Transactional
    @PostConstruct
    public void initTipoTarjeta() {
        if (tipoTarjetaRepository.count() == 0) {
            TipoTarjetaCredito visa = TipoTarjetaCredito.of("VISA");
            TipoTarjetaCredito americanExpress = TipoTarjetaCredito.of("AMERICAN EXPRESS");
            TipoTarjetaCredito masterCard = TipoTarjetaCredito.of("MASTER CARD");
            tipoTarjetaRepository.saveAll(List.of(visa, americanExpress, masterCard));
        }
    }

    @Transactional
    @PostConstruct
    public void initAdministrador() {
        if (administradorRepository.count() == 0) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            Administrador admin1 = new Administrador();
            admin1.setEmail("pablo@pablo.com");
            admin1.setContrasena(passwordEncoder.encode("pablo"));
            administradorRepository.save(admin1);

            Administrador admin2 = new Administrador();
            admin2.setEmail("josea@josea.com");
            admin2.setContrasena(passwordEncoder.encode("josea"));
            administradorRepository.save(admin2);

            Administrador admin3 = new Administrador();
            admin3.setEmail("juanm@juanm.com");
            admin3.setContrasena(passwordEncoder.encode("juanm"));
            administradorRepository.save(admin3);
        }
    }

    @Transactional
    @PostConstruct
    public void initUsuario() {
        if (usuarioRepository.count() == 0) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            Usuario u1 = new Usuario();
            u1.setEmail("juanm@juanm.com");
            u1.setContrasena(passwordEncoder.encode("juanm"));
            u1.setCuentaBloqueada(true);
            u1.setMotivoBloqueo("BLOQUEO POR DEFECTO");
            u1.setTiempoHastaDesbloqueo(LocalDateTime.now().plus(Period.ofDays(1)));

            Usuario u2 = new Usuario();
            u2.setEmail("juanito@juanito.com");
            u2.setContrasena(passwordEncoder.encode("juanito"));
            u2.setCuentaBloqueada(true);
            u2.setMotivoBloqueo("BLOQUEO POR DEFECTO");
            u2.setTiempoHastaDesbloqueo(LocalDateTime.now().plus(Period.ofDays(2)));
            usuarioRepository.saveAll(List.of(u1, u2));
        }
    }

}
