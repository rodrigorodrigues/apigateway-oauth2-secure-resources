package com.jwks.autoconfigure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableConfigurationProperties(MultipleJwksProperties.class)
public class MultipleJwksAutoConfiguration {
    @Primary
    @Bean
    MultipleJwksJwtDecoder multipleJwksJwtDecoder(MultipleJwksProperties multipleJwksProperties) {
        return new MultipleJwksJwtDecoder(multipleJwksProperties);
    }
}
