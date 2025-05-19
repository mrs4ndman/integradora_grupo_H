package org.grupo_h.empleados.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FotografiaServiceImpl implements FotografiaService {


    public void subirFotografia(
            String carpetaDeSubida,
            String nombreArchivo,
            MultipartFile fotoSubida) throws IOException {

        if (fotoSubida == null || fotoSubida.isEmpty()) {
            throw new IOException("No se ha proporcionado ningún archivo en el servicio de la foto.");
        }

        Path carpeta = Paths.get(carpetaDeSubida);

        if (!Files.exists(carpeta)) {
            Files.createDirectories(carpeta);
        }

        try (InputStream inputStream = fotoSubida.getInputStream()) {
            Path ruta = carpeta.resolve(nombreArchivo);
            Files.copy(inputStream, ruta, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Error al guardar la fotografía", e);
        }
    }
}

