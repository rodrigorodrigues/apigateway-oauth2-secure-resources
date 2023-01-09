package com.example.springcloudgatewayoauth2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringCloudGatewayOauth2Application {
    private final Logger log = LoggerFactory.getLogger(SpringCloudGatewayOauth2Application.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudGatewayOauth2Application.class, args);
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder routeBuilder) {
        return routeBuilder.routes()
                .route("home",
                        route -> route
                                .path("/")
                                .uri("http://localhost:8082/home")
                )
                .build();
    }

}
