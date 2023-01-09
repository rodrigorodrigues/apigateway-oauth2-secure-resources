package com.example.frontendservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
@EnableWebSecurity
public class FrontendServiceApplication implements WebMvcConfigurer {
    @Autowired
    private DefaultCookieSerializer defaultCookieSerializer;

    @Value("${gateway-url:http://localhost:8080}")
    private String gatewayUrl;

    @PostConstruct
    public void init(){
        this.defaultCookieSerializer.setUseBase64Encoding(false);
    }

    public static void main(String[] args) {
        SpringApplication.run(FrontendServiceApplication.class, args);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .authorizeHttpRequests()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic().disable()
                .formLogin().disable()
                .requestCache().disable() //this is really important for gateway session propagation works.
                .build();
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/home", "/");
    }

    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Controller
    public class HomeController {
        private final Logger log = LoggerFactory.getLogger(HomeController.class);
        private final ObjectMapper objectMapper;

        private final RestTemplate restTemplate;

        public HomeController(ObjectMapper objectMapper, RestTemplate restTemplate) {
            this.objectMapper = objectMapper;
            this.restTemplate = restTemplate;
        }

        @GetMapping("/")
        public String home(Authentication authentication, Model model) throws JsonProcessingException {
            if (authentication.getPrincipal() instanceof OidcUser o) {
                model.addAttribute("name", o.getFullName());
                model.addAttribute("details", o.getAttributes());
                model.addAttribute("authenticationObj", objectMapper.writeValueAsString(o.getIdToken()));
            } else {
                model.addAttribute("name", authentication.getName());
                model.addAttribute("authenticationObj", objectMapper.writeValueAsString(authentication.getPrincipal()));
            }
            addOrders(authentication, model);
            model.addAttribute("gatewayUrl", gatewayUrl);
            return "home";
        }

        @GetMapping("/taskPage")
        public String tasks(@RequestParam("json") String json, Model model) {
            try {
                model.addAttribute("gatewayUrl", gatewayUrl);
                model.addAttribute("tasks", Arrays.asList(objectMapper.readValue(json, TaskDto[].class)));
            } catch (Exception e) {
                log.warn("Could not load tasks", e);
                model.addAttribute("msgTaskError", e.getLocalizedMessage());
            }
            return "task";
        }

        private void addOrders(Authentication authentication, Model model) {
            try {
                HttpHeaders httpHeaders = new HttpHeaders();
                if (authentication.getPrincipal() instanceof OidcUser o) {
                    httpHeaders.setBearerAuth(o.getIdToken().getTokenValue());
                } else {
                    httpHeaders.setBearerAuth(objectMapper.writeValueAsString(authentication.getPrincipal()));
                }
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity httpEntity = new HttpEntity<>(httpHeaders);
                model.addAttribute("orders", restTemplate.exchange("http://localhost:8080/v1/orders", HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<OrderDto>>() {
                }).getBody());
            } catch (Exception e) {
                log.warn("Could not get orders", e);
                model.addAttribute("msgOrderError", e.getLocalizedMessage());
            }
        }
    }

    static class OrderDto {
        private String orderId;
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

    static class TaskDto {
        private String taskId;
        private String name;
        private String user = "default";

        public TaskDto(UUID taskId, String name) {
            this.taskId = taskId.toString();
            this.name = name;
        }

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }
    }
}
