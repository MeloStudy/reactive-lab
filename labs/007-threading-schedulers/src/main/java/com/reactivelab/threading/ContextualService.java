package com.reactivelab.threading;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.Context;

/**
 * Scenario 3: Demonstrates how to propagate state across threads using Context.
 */
public class ContextualService {

    public static final String CORRELATION_ID_KEY = "correlationId";

    /**
     * Tries to read a correlation ID from the context even after a thread jump.
     */
    public Mono<String> getCorrelationIdAfterHop() {
        return Mono.deferContextual(ctx -> {
                    String id = ctx.getOrDefault(CORRELATION_ID_KEY, "NOT_FOUND");
                    return Mono.just(id);
                })
                .publishOn(Schedulers.parallel())
                .map(id -> "Correlation ID [" + id + "] found on thread " + Thread.currentThread().getName());
    }

    /**
     * Helper to wrap a value in the Reactor Context.
     */
    public Mono<String> runWithContext(String id) {
        return getCorrelationIdAfterHop()
                .contextWrite(Context.of(CORRELATION_ID_KEY, id));
    }
}
