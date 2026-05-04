package com.reactivelab.testing;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import java.time.Duration;

public class BuggyService {

    /**
     * Scenario 2: A pipeline that fails with a null pointer exception.
     * The assembly and execution are separated, making the stack trace cryptic.
     */
    public Flux<String> getUpperCaseData(Flux<String> input) {
        return input
                .map(s -> {
                    if (s == null || s.equals("CRASH")) {
                        return null; // This will trigger a NPE deep in Reactor
                    }
                    return s.toUpperCase();
                })
                .filter(s -> !s.isEmpty());
    }

    /**
     * Scenario 3: Demonstrating Context propagation.
     */
    public Mono<String> processWithContext(String data) {
        return Mono.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault("correlation-id", "unknown");
            return Mono.just("Processed [" + data + "] with ID: " + correlationId);
        });
    }

    /**
     * Scenario 4: A pipeline that accidentally blocks.
     */
    public Flux<Integer> blockingPipeline(Flux<Integer> input) {
        return input
                .map(i -> {
                    try {
                        Thread.sleep(10); // THIS IS FORBIDDEN ON EVENT LOOP
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return i * 2;
                });
    }
}
