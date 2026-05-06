package com.reactivelab.threading;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.Context;

/**
 * Scenario 3: Demonstrates how to propagate state across threads using Context.
 */
@Slf4j
public class ContextualService {

    public static final String CORRELATION_ID_KEY = "correlationId";

    /**
     * Reads a correlation ID from the context even after a thread jump.
     */
    public Mono<String> getCorrelationIdAfterHop() {
        return Mono.deferContextual(ctx -> {
                    String id = ctx.getOrDefault(CORRELATION_ID_KEY, "NOT_FOUND");
                    log.info("Reading context value: {} on thread: {}", id, Thread.currentThread().getName());
                    return Mono.just(id);
                })
                .publishOn(Schedulers.parallel())
                .map(id -> {
                    String threadName = Thread.currentThread().getName();
                    log.info("After thread hop, ID is still: {} on thread: {}", id, threadName);
                    return "Correlation ID [" + id + "] on " + threadName;
                });
    }

    /**
     * Helper to wrap a value in the Reactor Context.
     */
    public Mono<String> runWithContext(String id) {
        return getCorrelationIdAfterHop()
                .contextWrite(Context.of(CORRELATION_ID_KEY, id));
    }
}
