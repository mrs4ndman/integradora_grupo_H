package org.grupo_h.administracion.service;

import org.grupo_h.comun.entity.Parametros;
import org.grupo_h.comun.repository.ParametrosRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ParametrosServiceImpl implements ParametrosService {

    private static final Logger log = LoggerFactory.getLogger(ParametrosServiceImpl.class);

    private static final String KEY_MAX_INTENTOS = "MAX_INTENTOS_FALLIDOS";
    private static final String KEY_DURACION_BLOQUEO = "DURACION_BLOQUEO";
    private static final String KEY_DURACION_BLOQUEO_ADMIN = "DURACION_BLOQUEO_ADMIN";

    @Autowired
    private ParametrosRepository parametrosRepository;


    /**
     * Método privado para obtener y parsear un parámetro a valor entero.
     *
     * @param clave Clave de tipo {@link String} del parámetro a buscar en la base de datos
     * @return {@link Optional} con el valor entero si existe y es válido, o vacío si no se encuentra o no es convertible
     */
    private Optional<Integer> getParametroComoInt(String clave) {
        Optional<Parametros> parametroOpt = parametrosRepository.findByClave(clave);
        if (parametroOpt.isPresent()) {
            try {
                return Optional.of(Integer.parseInt(parametroOpt.get().getValor()));
            } catch (NumberFormatException e) {
                log.error("Error al convertir el valor del parámetro '{}' a Integer. Valor encontrado: '{}'",
                        clave, parametroOpt.get().getValor(), e);
                return Optional.empty(); // Falla la conversión
            }
        }
        log.warn("Parámetro requerido '{}' no encontrado en la base de datos.", clave);
        return Optional.empty(); // No se encontró la clave
    }

    /**
     * Obtiene el número máximo de intentos fallidos permitidos antes de bloquear una cuenta.
     * Este valor se almacena en caché para mejorar el rendimiento.
     *
     * @return Número entero con el máximo de intentos fallidos configurado
     * @throws {@link IllegalStateException} si el parámetro no existe o no es válido
     */
    @Override
    @Cacheable(value = "parametros", key = "'" + KEY_MAX_INTENTOS + "'")
    public int getMaxIntentosFallidos() {
        log.debug("Obteniendo parámetro: {}", KEY_MAX_INTENTOS);
        // Intenta obtener el valor. Si no existe o es inválido, lanza una excepción.
        return getParametroComoInt(KEY_MAX_INTENTOS)
                .orElseThrow(() -> new IllegalStateException(
                        "Parámetro requerido '" + KEY_MAX_INTENTOS + "' no encontrado o inválido en la base de datos."));
    }

    /**
     * Obtiene la duración del bloqueo en minutos para usuarios regulares.
     * Este valor se almacena en caché para mejorar el rendimiento.
     *
     * @return Número entero con la duración del bloqueo en minutos
     * @throws {@link IllegalStateException} si el parámetro no existe o no es válido
     */
    @Override
    @Cacheable(value = "parametros", key = "'" + KEY_DURACION_BLOQUEO + "'")
    public int getDuracionBloqueoMinutos() {
        log.debug("Obteniendo parámetro: {}", KEY_DURACION_BLOQUEO);
        // Intenta obtener el valor. Si no existe o es inválido, lanza una excepción.
        return getParametroComoInt(KEY_DURACION_BLOQUEO)
                .orElseThrow(() -> new IllegalStateException(
                        "Parámetro requerido '" + KEY_DURACION_BLOQUEO + "' no encontrado o inválido en la base de datos."));
    }

    /**
     * Obtiene la duración del bloqueo en minutos para administradores.
     * Este valor se almacena en caché para mejorar el rendimiento.
     *
     * @return Número entero con la duración del bloqueo en minutos para administradores
     * @throws {@link IllegalStateException} si el parámetro no existe o no es válido
     */
    @Override
    @Cacheable(value = "parametros", key = "'" + KEY_DURACION_BLOQUEO_ADMIN + "'")
    public int getDuracionBloqueoMinutosAdmin() {
        log.debug("Obteniendo parámetro: {}", KEY_DURACION_BLOQUEO_ADMIN);
        return getParametroComoInt(KEY_DURACION_BLOQUEO_ADMIN)
                .orElseThrow(() -> new IllegalStateException(
                        "Parámetro requerido '" + KEY_DURACION_BLOQUEO_ADMIN + "' no encontrado o inválido en la base de datos."));
    }

    /**
     * Limpia la caché de parámetros, forzando a que las próximas solicitudes
     * obtengan los valores actualizados desde la base de datos.
     */
    @CacheEvict(value = "parametros", allEntries = true)
    public void limpiarCacheParametros() {
        log.info("Caché de parámetros limpiada.");
    }
}