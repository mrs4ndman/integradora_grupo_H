package org.grupo_h.empleados.service;

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

    @Autowired
    private ParametrosRepository parametrosRepository;


    // Método privado para obtener y parsear el valor. Devuelve Optional vacío si falla.
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

    @Override
    @Cacheable(value = "parametros", key = "'" + KEY_MAX_INTENTOS + "'")
    public int getMaxIntentosFallidos() {
        log.debug("Obteniendo parámetro: {}", KEY_MAX_INTENTOS);
        // Intenta obtener el valor. Si no existe o es inválido, lanza una excepción.
        return getParametroComoInt(KEY_MAX_INTENTOS)
                .orElseThrow(() -> new IllegalStateException(
                        "Parámetro requerido '" + KEY_MAX_INTENTOS + "' no encontrado o inválido en la base de datos."));
    }

    @Override
    @Cacheable(value = "parametros", key = "'" + KEY_DURACION_BLOQUEO + "'")
    public int getDuracionBloqueoMinutos() {
        log.debug("Obteniendo parámetro: {}", KEY_DURACION_BLOQUEO);
        // Intenta obtener el valor. Si no existe o es inválido, lanza una excepción.
        return getParametroComoInt(KEY_DURACION_BLOQUEO)
                .orElseThrow(() -> new IllegalStateException(
                        "Parámetro requerido '" + KEY_DURACION_BLOQUEO + "' no encontrado o inválido en la base de datos."));
    }

    @CacheEvict(value = "parametros", allEntries = true)
    public void limpiarCacheParametros() {
        log.info("Caché de parámetros limpiada.");
    }
}