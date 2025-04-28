package org.grupo_h.empleados.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfiguration {

    // Mapeados de Entidades a DTO
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
}
