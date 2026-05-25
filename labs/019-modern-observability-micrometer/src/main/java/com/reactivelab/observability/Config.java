package com.reactivelab.observability;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
class Config {
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        // The builder provided by Spring Boot Actuator is already instrumented
        return builder.build();
    }
}
