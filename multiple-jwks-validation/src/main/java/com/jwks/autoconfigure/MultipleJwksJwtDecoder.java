package com.jwks.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.util.Map;
import java.util.stream.Collectors;

public class MultipleJwksJwtDecoder implements JwtDecoder {
    private static final Logger log = LoggerFactory.getLogger(MultipleJwksJwtDecoder.class);

    private final Map<String, JwtDecoder> multipleJwksDecoders;

    public MultipleJwksJwtDecoder(MultipleJwksProperties multipleJwksProperties) {
        this.multipleJwksDecoders = multipleJwksProperties.getMultipleJwks()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getValue, v -> NimbusJwtDecoder.withJwkSetUri(v.getValue()).build()));
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        Jwt jwt = null;
        int i = 0;
        int size = multipleJwksDecoders.size();
        for (Map.Entry<String, JwtDecoder> entry : multipleJwksDecoders.entrySet()) {
            try {
                JwtDecoder jwtDecoder = entry.getValue();
                jwt = jwtDecoder.decode(token);
                break;
            } catch (Exception e) {
                if (++i == size) {
                    log.error("Could not validate token by jwks endpoint: {}", entry.getKey(), e);
                    throw e;
                } else {
                    log.info("Could not validate token by jwks endpoint: {}, trying for the next one", entry.getKey());
                }
            }
        }
        return jwt;
    }
}
