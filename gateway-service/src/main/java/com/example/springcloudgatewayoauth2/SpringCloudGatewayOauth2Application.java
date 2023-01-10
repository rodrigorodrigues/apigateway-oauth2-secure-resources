package com.example.springcloudgatewayoauth2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringCloudGatewayOauth2Application {
    public static void main(String[] args) {
        SpringApplication.run(SpringCloudGatewayOauth2Application.class, args);
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder routeBuilder,
                               @Value("${FRONTEND_URL:http://localhost:8082}") String frontendUrl) {
        return routeBuilder.routes()
                .route("home",
                        route -> route
                                .path("/")
                                .uri(String.format("%s/home", frontendUrl))
                )
                .build();
    }

}
