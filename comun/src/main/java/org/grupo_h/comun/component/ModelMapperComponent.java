package org.grupo_h.comun.component;


import jakarta.annotation.PostConstruct;
import org.grupo_h.comun.entity.Empleado;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ModelMapperComponent {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }



}
