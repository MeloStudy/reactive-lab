package com.reactivelab.resilience.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
public class CorrelationIdFilter implements WebFilter {

    public static final String CORRELATION_ID_KEY = "X-Correlation-ID";

    /**
     * Intercepts incoming requests to inject/generate a unique Correlation ID.
     * Propagates this ID via both Exchange Attributes and Reactor Context.
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String correlationId = exchange.getRequest().getHeaders().getFirst(CORRELATION_ID_KEY);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = "gen-" + UUID.randomUUID();
            log.trace("No X-Correlation-ID header found. Generated new trace ID: {}", correlationId);
        } else {
            log.trace("Found existing X-Correlation-ID header: {}", correlationId);
        }

        // Write to exchange attributes for non-reactive filter retrieval compatibility
        exchange.getAttributes().put(CORRELATION_ID_KEY, correlationId);

        final String finalCorrelationId = correlationId;
        return chain.filter(exchange)
                .doOnEach(signal -> {
                    if (signal.isOnComplete() || signal.isOnError()) {
                        log.trace("Request lifecycle signal [{}] captured for correlation: {}", signal.getType(), finalCorrelationId);
                    }
                })
                // Propagate to Reactor Context. This is critical for downstream event-loop hops.
                .contextWrite(context -> context.put(CORRELATION_ID_KEY, finalCorrelationId));
    }
}
