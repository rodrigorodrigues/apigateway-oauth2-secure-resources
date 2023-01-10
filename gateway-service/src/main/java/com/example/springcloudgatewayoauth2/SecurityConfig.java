package com.example.springcloudgatewayoauth2;

import com.jwks.autoconfigure.MultipleJwksJwtDecoder;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, OAuth2ClientProperties oAuth2ClientProperties) {
        http.authorizeExchange()
                .pathMatchers("/actuator/**").permitAll()
                .anyExchange()
                .authenticated()
                .and().formLogin();
        if (!oAuth2ClientProperties.getRegistration().isEmpty()) {
            http.oauth2Login(l -> l.authorizedClientRepository(authorizedClientRepository()))
                    .oauth2ResourceServer().jwt();
        }
        return http.build();
    }

    @Bean
    ReactiveJwtDecoder reactiveJwtDecoder(MultipleJwksJwtDecoder multipleJwksJwtDecoder) {
        return token -> Mono.just(multipleJwksJwtDecoder.decode(token));
    }

    @Bean
    public ServerOAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new WebSessionServerOAuth2AuthorizedClientRepository();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    MapReactiveUserDetailsService inMemoryUserDetailsManager() {
        UserDetails user = User.builder()
                .username("user")
                .password("{noop}password")
                .roles("USER")
                .build();
        UserDetails admin = User.builder()
                .username("admin")
                .password("{noop}password")
                .roles("USER", "ADMIN")
                .build();
        return new MapReactiveUserDetailsService(user, admin);
    }
}
