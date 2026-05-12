package com.reactivelab.web.router;

import com.reactivelab.web.handler.StockHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Slf4j
@Configuration
public class StockRouter {

    @Bean
    public RouterFunction<ServerResponse> functionalRoutes(StockHandler handler) {
        return route(GET("/functional/stocks/{symbol}").and(accept(MediaType.APPLICATION_JSON)), handler::getQuote)
                .andRoute(GET("/functional/stocks/{symbol}/stream").and(accept(MediaType.TEXT_EVENT_STREAM)), handler::getStream)
                .filter((request, next) -> {
                    long start = System.currentTimeMillis();
                    return next.handle(request).doOnNext(res -> {
                        long duration = System.currentTimeMillis() - start;
                        log.info("Functional route [{}] executed in {}ms", request.path(), duration);
                        request.exchange().getResponse().getHeaders().add("X-Execution-Time", duration + "ms");
                        request.exchange().getResponse().getHeaders().add("X-Security-Header", "Active");
                    });
                });

    }
}
