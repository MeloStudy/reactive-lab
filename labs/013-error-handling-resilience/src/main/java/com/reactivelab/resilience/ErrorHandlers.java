package com.reactivelab.resilience;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes, 
                                          WebProperties webProperties,
                                          ApplicationContext applicationContext,
                                          ServerCodecConfigurer configurer) {
        super(errorAttributes, webProperties.getResources(), applicationContext);
        this.setMessageWriters(configurer.getWriters());
        this.setMessageReaders(configurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Throwable error = getError(request);
        log.error("Global error caught: {}", error.getMessage());

        HttpStatus status = (error instanceof ProductNotFoundException) ? HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
        
        // Scenario 3 & 4: RFC 7807 Problem Detail + Context Correlation ID
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, error.getMessage());
        problem.setTitle("Reactive Lab Error");
        
        String correlationId = request.attribute("X-Correlation-ID")
                .map(Object::toString)
                .orElse("N/A");
        problem.setProperty("correlation_id", correlationId);

        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .bodyValue(problem);
    }
}

@Component
class CorrelationIdFilter implements org.springframework.web.server.WebFilter {
    @Override
    public Mono<Void> filter(org.springframework.web.server.ServerWebExchange exchange, 
                             org.springframework.web.server.WebFilterChain chain) {
        String correlationId = exchange.getRequest().getHeaders().getFirst("X-Correlation-ID");
        if (correlationId == null) correlationId = "gen-" + java.util.UUID.randomUUID();
        
        exchange.getAttributes().put("X-Correlation-ID", correlationId);
        
        final String finalCorrelationId = correlationId;
        return chain.filter(exchange)
                .contextWrite(ctx -> ctx.put("X-Correlation-ID", finalCorrelationId));
    }
}
