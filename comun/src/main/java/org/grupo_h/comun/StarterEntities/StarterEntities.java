//package org.grupo_h.comun.StarterEntities;
//
//import jakarta.annotation.PostConstruct;
//import jakarta.transaction.Transactional;
//import org.grupo_h.comun.entity.*;
//import org.grupo_h.comun.entity.auxiliar.*;
//import org.grupo_h.comun.repository.*;
//import jakarta.transaction.Transactional;
//import org.grupo_h.comun.entity.Departamento;
//import org.grupo_h.comun.entity.EspecialidadesEmpleado;
//import org.grupo_h.comun.entity.auxiliar.*;
//import org.grupo_h.comun.repository.*;
//import org.springframework.beans.factory.annotation.Autowired;
////  import org.springframework.boot.CommandLineRunner;
////  org.springframework.context.annotation.Bean;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//import java.time.Period;
//import java.util.List;
//
//@Component
//public class StarterEntities {
//
//    @Autowired
//    private GeneroRepository generoRepository;
//
//    @Autowired
//    private PaisRepository paisRepository;
//
//    @Autowired
//    private TipoViaRepository tipoViaRepository;
//
//    @Autowired
//    private ParametrosRepository parametrosRepository;
//
//    @Autowired
//    private TipoDocumentoRepository tipoDocumentoRepository;
//
////    @Autowired
////    private CuentaCorrienteRepository cuentaCorrienteRepository;
//
//    @Autowired
//    private EntidadBancariaRepository entidadBancariaRepository;
//
//    @Autowired
//    private DepartamentoRepository departamentoRepository;
//
//    @Autowired
//    private EspecialidadesEmpleadoRepository especialidadesEmpleadoRepository;
//
//    @Autowired
//    private TipoTarjetaRepository tipoTarjetaRepository;
//
//    @Autowired
//    private AdministradorRepository administradorRepository;
//
//    @Autowired
//    private UsuarioRepository usuarioRepository;
//
//    @Autowired
//    private ProveedorRepository proveedorRepository;
//
//    @PostConstruct
//    public void initGenero() {
//        if (generoRepository.count() == 0) {
//            generoRepository.save(new Genero("M", "Masculino"));
//            generoRepository.save(new Genero("F", "Femenino"));
//            generoRepository.save(new Genero("O", "Otro"));
//        }
//    }
//
//    @PostConstruct
//    public void initPais() {
//        if (paisRepository.count() == 0) {
//            paisRepository.save(new Pais("ES", "España", "+34"));
//            paisRepository.save(new Pais("FR", "Francia", "+33"));
//            paisRepository.save(new Pais("US", "Estados Unidos", "+1"));
//            paisRepository.save(new Pais("IT", "Italia", "+39"));
//            paisRepository.save(new Pais("AN", "Andorra", "+376"));
//        }
//    }
//
//    @PostConstruct
//    public void initTipoVia() {
//        if (tipoViaRepository.count() == 0) {
//            tipoViaRepository.save(new TipoVia("AV", "Avenida"));
//            tipoViaRepository.save(new TipoVia("C", "Calle"));
//            tipoViaRepository.save(new TipoVia("PS", "Paseo"));
//            tipoViaRepository.save(new TipoVia("PA", "Pasaje"));
//        }
//    }
//
//    @PostConstruct
//    public void initTipoDocumento() {
//        if (tipoDocumentoRepository.count() == 0) {
//            tipoDocumentoRepository.save(new TipoDocumento("DNI", "Documento Nacional Identidad "));
//            tipoDocumentoRepository.save(new TipoDocumento("NIE", "Numero Identidad Extranjero"));
//        }
//    }
//
//
//    @PostConstruct
//    public void initEntidadBancaria() {
//        if (entidadBancariaRepository.count() == 0) {
//            try {
//
//                // 1. Verificar existencia del país (con debug)
//                System.err.println("Buscando país ES...");
//                Pais espana = paisRepository.findById("ES")
//                        .orElseThrow(() -> {
//                            System.err.println("País ES no encontrado. Países existentes:");
//                            paisRepository.findAll().forEach(p ->
//                                    System.err.println(p.getCodigoPais() + " - " + p.getNombrePais()));
//                            return new IllegalArgumentException("Pais con código ES no encontrado");
//                        });
//
//                EntidadBancaria santander = new EntidadBancaria();
//                santander.setSiglas("SAN");
//                santander.setNombre("Banco Santander");
//                santander.setCodigoEntidad("0049");
//                santander.setPais(espana);
//
//                EntidadBancaria bbva = new EntidadBancaria();
//                bbva.setCodigoEntidad("0812");
//                bbva.setNombre("Banco Bilbao Vizcaya Argentaria");
//                bbva.setSiglas("BBVA");
//                bbva.setPais(espana);
//
//                EntidadBancaria caixabank = new EntidadBancaria();
//                caixabank.setCodigoEntidad("2100");
//                caixabank.setNombre("Caixabank");
//                caixabank.setSiglas("CABK");
//                caixabank.setPais(espana);
//
//                EntidadBancaria sabadell = new EntidadBancaria();
//                sabadell.setCodigoEntidad("0081");
//                sabadell.setNombre("Sabadell");
//                sabadell.setSiglas("SABE");
//                sabadell.setPais(espana);
//
//                entidadBancariaRepository.saveAll(List.of(santander, bbva, caixabank, sabadell));
//
//
//                System.out.println("Datos iniciales de EntidadBancaria cargados exitosamente");
//            } catch (Exception e) {
//                System.err.println("Error al cargar datos iniciales: " + e.getMessage());
//            }
//        }
//    }
//
//    @PostConstruct
//    public void initParametros() {
//        if (parametrosRepository.count() == 0) {
//            parametrosRepository.save(new Parametros("MAX_INTENTOS_FALLIDOS", "3"));
//            parametrosRepository.save(new Parametros("DURACION_BLOQUEO", "15"));
//            parametrosRepository.save(new Parametros("DURACION_BLOQUEO_ADMIN", "1440"));
//        }
//    }
//
//    @PostConstruct
//    public void initDepartamento() {
//        if (departamentoRepository.count() == 0) {
//            departamentoRepository.save(new Departamento("PREP", "Preparación de Pedidos"));
//            departamentoRepository.save(new Departamento("ALMAC", "Almacenaje"));
//            departamentoRepository.save(new Departamento("RECEP", "Recepción de Mercancías"));
//            departamentoRepository.save(new Departamento("VENTA", "Ventas"));
//            departamentoRepository.save(new Departamento("ADMIN", "Administración"));
//        }
//
//    }
//
//    @Transactional
//    @PostConstruct
//    public void initEspecialidadEmpleado() {
//        if (especialidadesEmpleadoRepository.count() == 0) {
//            EspecialidadesEmpleado especialidad1 = EspecialidadesEmpleado.of("Especialista en Gestión de Inventarios");
//            EspecialidadesEmpleado especialidad2 = EspecialidadesEmpleado.of("Especialista en IT");
//            EspecialidadesEmpleado especialidad3 = EspecialidadesEmpleado.of("Especialista en Logística");
//            especialidadesEmpleadoRepository.saveAll(List.of(especialidad1, especialidad2, especialidad3));
//        }
//    }
//
//    @Transactional
//    @PostConstruct
//    public void initTipoTarjeta() {
//        if (tipoTarjetaRepository.count() == 0) {
//            TipoTarjetaCredito visa = TipoTarjetaCredito.of("VISA", "V");
//            TipoTarjetaCredito americanExpress = TipoTarjetaCredito.of("AMERICAN EXPRESS", "AM");
//            TipoTarjetaCredito masterCard = TipoTarjetaCredito.of("MASTER CARD", "MC");
//            tipoTarjetaRepository.saveAll(List.of(visa, americanExpress, masterCard));
//        }
//    }
//
//    @Transactional
//    @PostConstruct
//    public void initAdministrador() {
//        if (administradorRepository.count() == 0) {
//            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//
//            Administrador admin1 = new Administrador();
//            admin1.setEmail("pablo@pablo.com");
//            admin1.setContrasena(passwordEncoder.encode("pablo"));
//            administradorRepository.save(admin1);
//
//            Administrador admin2 = new Administrador();
//            admin2.setEmail("josea@josea.com");
//            admin2.setContrasena(passwordEncoder.encode("josea"));
//            administradorRepository.save(admin2);
//
//            Administrador admin3 = new Administrador();
//            admin3.setEmail("juanm@juanm.com");
//            admin3.setContrasena(passwordEncoder.encode("juanm"));
//            administradorRepository.save(admin3);
//        }
//    }
//
//    @Transactional
//    @PostConstruct
//    public void initUsuario() {
//        if (usuarioRepository.count() == 0) {
//            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//            Usuario u1 = new Usuario();
//            u1.setEmail("juanm@juanm.com");
//            u1.setContrasena(passwordEncoder.encode("juanm"));
//            u1.setCuentaBloqueada(true);
//            u1.setMotivoBloqueo("BLOQUEO POR DEFECTO");
//            u1.setTiempoHastaDesbloqueo(LocalDateTime.now().plus(Period.ofDays(1)));
//
//            Usuario u2 = new Usuario();
//            u2.setEmail("juanito@juanito.com");
//            u2.setContrasena(passwordEncoder.encode("juanito"));
//            u2.setCuentaBloqueada(true);
//            u2.setMotivoBloqueo("BLOQUEO POR DEFECTO");
//            u2.setTiempoHastaDesbloqueo(LocalDateTime.now().plus(Period.ofDays(2)));
//            usuarioRepository.saveAll(List.of(u1, u2));
//        }
//    }
//
//    @Transactional // Buena práctica para asegurar atomicidad
//    @PostConstruct
//    public void initProveedores() {
//        if (proveedorRepository.count() == 0) {
//            System.out.println(">>> Inicializando Proveedores..."); // Log simple
//
//            Proveedor p1 = new Proveedor();
//            p1.setNombre("TecnoHogar S.A.");
//
//            Proveedor p2 = new Proveedor();
//            p2.setNombre("Lecturas Milenio");
//
//            Proveedor p3 = new Proveedor();
//            p3.setNombre("Productalia");
//
//            Proveedor p4 = new Proveedor();
//            p4.setNombre("Acme");
//
//            Proveedor p5 = new Proveedor();
//            p5.setNombre("Papeleria");
//
//
//            // Guardar todos los proveedores nuevos
//            try {
//                proveedorRepository.saveAll(List.of(p1, p2, p3, p4, p5));
//            } catch (Exception e) {
//                System.err.println("Error al inicializar proveedores: " + e.getMessage());
//            }
//        }
//    }
//
//}
