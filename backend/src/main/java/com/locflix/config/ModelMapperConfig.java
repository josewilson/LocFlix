package com.locflix.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do ModelMapper para conversão de DTOs e Entidades.
 */
@Configuration
public class ModelMapperConfig {

    /**
     * Cria um bean de ModelMapper com configurações estratégicas.
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Estratégia de correspondência: apenas propriedades exatamente iguais
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true);

        return modelMapper;
    }
}

