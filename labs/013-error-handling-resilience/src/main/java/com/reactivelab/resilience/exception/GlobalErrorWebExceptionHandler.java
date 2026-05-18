package com.reactivelab.resilience.exception;

import com.reactivelab.resilience.model.ProductNotFoundException;
import com.reactivelab.resilience.filter.CorrelationIdFilter;
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

@Slf4j
@Component
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

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

    /**
     * Scenario 3 & 4: Renders error response using RFC 7807 ProblemDetail
     * and extracts correlation trace ID.
     */
    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Throwable error = getError(request);
        log.error("Global Web Exception Handler intercepted error: {}", error.getMessage());

        HttpStatus status = (error instanceof ProductNotFoundException) ? HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
        
        // Scenario 3: Standardize on RFC 7807 Problem Detail response
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, error.getMessage());
        problem.setTitle("Reactive Lab Error");
        
        // Scenario 4: Context Propagation retrieval
        // We attempt to retrieve the Correlation-ID from the Reactor Context first.
        // However, because contextWrite inside a WebFilter propagates upstream,
        // and WebFlux handles global exceptions downstream in HttpWebHandlerAdapter,
        // the Reactor Context is empty at the WebExceptionHandler subscription boundary.
        // Thus, we fall back to exchange attributes to ensure reliable global mapping.
        return Mono.deferContextual(context -> {
            String correlationId = context.getOrDefault(CorrelationIdFilter.CORRELATION_ID_KEY, null);
            
            if (correlationId == null) {
                correlationId = request.attribute(CorrelationIdFilter.CORRELATION_ID_KEY)
                        .map(Object::toString)
                        .orElse("N/A");
                log.debug("Reactor Context was empty at exception boundary. Resolved correlation_id [{}] from exchange attributes.", correlationId);
            } else {
                log.info("Successfully extracted correlation_id [{}] from Reactor Context.", correlationId);
            }
            
            problem.setProperty("correlation_id", correlationId);

            return ServerResponse.status(status)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .bodyValue(problem);
        });
    }
}
