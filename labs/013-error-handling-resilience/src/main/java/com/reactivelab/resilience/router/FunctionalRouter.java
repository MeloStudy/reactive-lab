package com.reactivelab.resilience.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class FunctionalRouter {

    /**
     * Scenario 7: Filter-Level Error validation endpoint.
     * Configures a functional route at /api/resilience/secure that performs intercepting key validation.
     * If the required header is missing or incorrect, it propagates a RuntimeException down the reactive pipeline,
     * which will then be caught by the Global Web Exception Handler.
     */
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
