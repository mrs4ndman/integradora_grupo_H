package org.grupo_h.administracion.service;

import org.springframework.web.multipart.MultipartFile;

public interface CatalogoService {
    /**
     * Importa un catálogo de productos desde un archivo JSON.
     *
     * @param archivoJson El archivo MultipartFile que contiene el catálogo en formato JSON.
     * @return Un mensaje indicando el resultado de la importación
     * @throws Exception Si ocurre un error durante la importación
     */
    String importarCatalogo(MultipartFile archivoJson) throws Exception;
}