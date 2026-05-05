package com.reactivelab.resilience;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
public class ResilienceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResilienceApplication.class, args);
    }

    // Scenario 7: Filter Error validation endpoint
    @Bean
    public RouterFunction<ServerResponse> functionalRoutes() {
        return route(GET("/api/resilience/secure"), request -> ServerResponse.ok().bodyValue("SECURE DATA"))
                .filter((request, next) -> {
                    if (!request.headers().header("X-Security-Key").contains("secret")) {
                        return Mono.error(new RuntimeException("Missing security key"));
                    }
                    return next.handle(request);
                });
    }
}
