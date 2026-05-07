package com.reactivelab.testing;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * Scenario 3: The Correlation ID (Context)
 * Demonstrates how to propagate and read state across thread hops.
 */
@Slf4j
public class ContextualTracer {

    /**
     * Reads a value from the Context and appends it to the stream.
     *
     * @param key The context key to read
     * @return A Flux that emits the value found in context
     */
    public Flux<String> getContextualData(String key) {
        return Flux.deferContextual(ctx -> {
            String value = ctx.getOrDefault(key, "NOT_FOUND");
            log.info("Reading context for key [{}]: {}", key, value);
            return Flux.just("Value: " + value);
        });
    }
}
