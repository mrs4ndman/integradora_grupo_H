package org.grupo_h.administracion.service;

import jakarta.transaction.Transactional;
import org.grupo_h.administracion.dto.AdministradorRegistroDTO;
import org.grupo_h.comun.entity.Administrador;
import org.grupo_h.comun.repository.AdministradorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Implementación del servicio para gestionar operaciones relacionadas con los administradors.
 */
@Service
public class AdministradorServiceImpl implements AdministradorService {

    private final AdministradorRepository administradorRepository;
    private final ParametrosService parametrosService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    /**
     * Constructor para inyectar las dependencias necesarias.
     *
     * @param administradorRepository Repositorio de administradors.
     * @param passwordEncoder         Codificador de contraseñas.
     */
    public AdministradorServiceImpl(AdministradorRepository administradorRepository, ParametrosService parametrosService, BCryptPasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.administradorRepository = administradorRepository;
        this.parametrosService = parametrosService;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    /**
     * Procesa un intento de inicio de sesión fallido.
     *
     * @param email Email que intentó iniciar sesión.
     */
    @Override
    @Transactional // Usar @Transactional de Spring o Jakarta según configuración
    public void procesarLoginFallido(String email) {
        Optional<Administrador> administradorOpt = administradorRepository.findByEmail(email);
        if (administradorOpt.isPresent()) {
            Administrador administrador = administradorOpt.get();
            if (administrador.isHabilitado() && !administrador.isCuentaBloqueada()) {
                administrador.setIntentosFallidos(administrador.getIntentosFallidos() + 1);

                // Obtener valores desde el servicio
                int maxIntentos = parametrosService.getMaxIntentosFallidos();
                int duracionBloqueo = parametrosService.getDuracionBloqueoMinutos();

                if (administrador.getIntentosFallidos() >= maxIntentos) { // Usar valor del servicio
                    administrador.setCuentaBloqueada(true);
                    LocalDateTime horaDesbloqueo = LocalDateTime.now().plus(duracionBloqueo, ChronoUnit.MINUTES); // Usar valor del servicio
                    administrador.setTiempoHastaDesbloqueo(horaDesbloqueo);
                    System.out.println("Cuenta bloqueada para administrador con email: " + email + ". Se desbloqueará a las: " + horaDesbloqueo);
                }
                administradorRepository.save(administrador);
            }
        }
    }

    /**
     * Procesa un inicio de sesión exitoso.
     *
     * @param email Email que inició sesión.
     */
    @Override
    @Transactional
    public void procesarLoginExitoso(String email) {
        Optional<Administrador> administradorOpt = administradorRepository.findByEmail(email);
        if (administradorOpt.isPresent()) {
            Administrador administrador = administradorOpt.get();
            if (administrador.getIntentosFallidos() > 0 || administrador.isCuentaBloqueada()) {
                administrador.setIntentosFallidos(0);
                administrador.setTiempoHastaDesbloqueo(null);
                administrador.setCuentaBloqueada(false);
                administradorRepository.save(administrador);
            }
        }
    }

    /**
     * Desbloquea una cuenta de administrador reseteando los intentos fallidos y el tiempo de bloqueo.
     *
     * @param email Email del administrador a desbloquear.
     */
    @Override
    @Transactional
    public void desbloquearCuenta(String email) {
        Optional<Administrador> administradorOpt = administradorRepository.findByEmail(email);
        if (administradorOpt.isPresent()) {
            Administrador administrador = administradorOpt.get();
            if (administrador.isCuentaBloqueada()) { // Solo si está bloqueada
                administrador.setCuentaBloqueada(false);
                administrador.setIntentosFallidos(0);
                administrador.setTiempoHastaDesbloqueo(null); // Resetea el tiempo de bloqueo
                administradorRepository.save(administrador);
            }
        }
    }
}
