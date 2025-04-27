package org.grupo_h.comun.StarterEntities;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.grupo_h.comun.entity.*;
import org.grupo_h.comun.entity.auxiliar.*;
import org.grupo_h.comun.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
//  import org.springframework.boot.CommandLineRunner;
//  org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

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

    @Autowired
    private CuentaCorrienteRepository cuentaCorrienteRepository;

    @Autowired
    private EntidadBancariaRepository entidadBancariaRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private EspecialidadesEmpleadoRepository especialidadesEmpleadoRepository;

    @Autowired
    private TipoTarjetaRepository tipoTarjetaRepository;

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
            tipoViaRepository.save(new TipoVia("AV", "Avenida"));
            tipoViaRepository.save(new TipoVia("C", "Calle"));
            tipoViaRepository.save(new TipoVia("PS", "Paseo"));
            tipoViaRepository.save(new TipoVia("PA", "Pasaje"));
        }
    }

    @PostConstruct
    public void initTipoDocumento() {
        if (tipoDocumentoRepository.count() == 0) {
            tipoDocumentoRepository.save(new TipoDocumento("DNI", "Documento Nacional Identidad "));
            tipoDocumentoRepository.save(new TipoDocumento("NIE", "Numero Identidad Extranjero"));
        }
    }


    @PostConstruct
    public void initEntidadBancaria() {
        if (entidadBancariaRepository.count() == 0) {
            try {

                // 1. Verificar existencia del país (con debug)
                System.err.println("Buscando país ES...");
                Pais espana = paisRepository.findById("ES")
                        .orElseThrow(() -> {
                            System.err.println("País ES no encontrado. Países existentes:");
                            paisRepository.findAll().forEach(p ->
                                    System.err.println(p.getCodigoPais() + " - " + p.getNombrePais()));
                            return new IllegalArgumentException("Pais con código ES no encontrado");
                        });

                EntidadBancaria santander = new EntidadBancaria();
                santander.setSiglasEntidad("SAN");
                santander.setNombreEntidadBancaria("Banco Santander");
                santander.setCodigoEntidadBancaria("0049");
                santander.setPais(espana);

                EntidadBancaria bbva = new EntidadBancaria();
                bbva.setCodigoEntidadBancaria("0812");
                bbva.setNombreEntidadBancaria("Banco Bilbao Vizcaya Argentaria");
                bbva.setSiglasEntidad("BBVA");
                bbva.setPais(espana);

                EntidadBancaria caixabank = new EntidadBancaria();
                caixabank.setCodigoEntidadBancaria("2100");
                caixabank.setNombreEntidadBancaria("Caixabank");
                caixabank.setSiglasEntidad("CABK");
                caixabank.setPais(espana);

                EntidadBancaria sabadell = new EntidadBancaria();
                sabadell.setCodigoEntidadBancaria("0081");
                sabadell.setNombreEntidadBancaria("Sabadell");
                sabadell.setSiglasEntidad("SABE");
                sabadell.setPais(espana);

                entidadBancariaRepository.saveAll(List.of(santander, bbva, caixabank, sabadell));


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
            EspecialidadesEmpleado especialidad = EspecialidadesEmpleado.of("Especialista en Gestión de Inventarios");
            especialidadesEmpleadoRepository.save(especialidad);
        }
    }

    @Transactional
    @PostConstruct
    public void initTipoTarjeta() {
        if (tipoTarjetaRepository.count() == 0) {
            TipoTarjetaCredito visa = TipoTarjetaCredito.of("VISA", "V");
            TipoTarjetaCredito americanExpress = TipoTarjetaCredito.of("AMERICAN EXPRESS", "AM");
            TipoTarjetaCredito masterCard = TipoTarjetaCredito.of("MASTER CARD", "MC");
            tipoTarjetaRepository.saveAll(List.of(visa, americanExpress, masterCard));
        }
    }
}
