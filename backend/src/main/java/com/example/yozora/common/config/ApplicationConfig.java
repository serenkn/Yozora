package com.example.yozora.common.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean // ModelMapperオブジェクトの作成
    protected ModelMapper modelmapper() {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper;
    }
}
