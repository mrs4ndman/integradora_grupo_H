package org.grupo_h.empleados.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
@Service
public interface FotografiaService {

    void subirFotografia(String carpetaDeSubida, String nombreArchivo, MultipartFile fotoSubida) throws IOException;
}
