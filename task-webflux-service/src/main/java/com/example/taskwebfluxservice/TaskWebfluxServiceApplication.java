package com.example.taskwebfluxservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
@EnableWebFluxSecurity
public class TaskWebfluxServiceApplication {
    private final Logger log = LoggerFactory.getLogger(TaskWebfluxServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TaskWebfluxServiceApplication.class, args);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.authorizeExchange()
                .anyExchange()
                .authenticated()
                .and().httpBasic().disable()
                .formLogin().disable()
                .build();
    }

    @RestController
    class TasksController {
        private final List<TaskDto> list = new ArrayList<>(Arrays.asList(new TaskDto(UUID.randomUUID(), "Learn Oauth2"),
                new TaskDto(UUID.randomUUID(), "Learn Python")));

        @GetMapping("/v1/tasks")
        public ResponseEntity<List<TaskDto>> getTasks(Authentication authentication) {
            log.info("Secured getTasks endpoint accessed by: {}", authentication.getName());
            return ResponseEntity.ok(list);
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
