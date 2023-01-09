package com.example.orderservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.*;

@SpringBootApplication
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class OrderServiceApplication {
    private final Logger log = LoggerFactory.getLogger(OrderServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf().disable()
                .authorizeHttpRequests()
                .anyRequest()
                .authenticated()
                .and()
                    .httpBasic().disable()
                    .formLogin().disable()
                .oauth2ResourceServer().jwt()
                .and().and()
                .build();
    }

    @RestController
    class OrderController {
        private final ObjectMapper objectMapper;

        private final List<OrderDto> list = new ArrayList<>(Arrays.asList(new OrderDto(UUID.randomUUID(), new BigDecimal("100.00")),
                new OrderDto(UUID.randomUUID(), new BigDecimal("29.89"))));

        OrderController(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @GetMapping("/v1/orders")
        public ResponseEntity<List<OrderDto>> getOrders(@AuthenticationPrincipal Jwt jwt) {
            log.info("Secured getOrders endpoint accessed by: {}", jwt.getClaimAsString("sub"));
            return ResponseEntity.ok(list);
        }

        @PostMapping("/v1/orders/issuer")
        public ResponseEntity<String> processOrder(@RequestBody OrderDto orderDto, @AuthenticationPrincipal Jwt jwt) throws JsonProcessingException {
            log.info("Processing orderId: {} for user: {}", orderDto.getOrderId(), jwt.getClaimAsString("sub"));
            orderDto.setUser(jwt.getClaimAsString("sub"));
            list.add(orderDto);

            return ResponseEntity.ok(objectMapper.writeValueAsString(Collections.singletonMap("status", "Success")));
        }
    }

    static class OrderDto {
        @NotBlank
        private String orderId;
        @NotNull
        private BigDecimal price;
        private String user = "default";

        public OrderDto(UUID orderId, BigDecimal price) {
            this.orderId = orderId.toString();
            this.price = price;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }
    }
}
