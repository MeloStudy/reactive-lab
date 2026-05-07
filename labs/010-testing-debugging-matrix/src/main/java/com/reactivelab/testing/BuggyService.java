package com.reactivelab.testing;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class BuggyService {

    /**
     * Scenario 2: A pipeline that fails with a null pointer exception.
     * The assembly and execution are separated, making the stack trace cryptic.
     */
    public Flux<String> getUpperCaseData(Flux<String> input) {
        return input
                .map(s -> {
                    if (s == null || s.equals("CRASH")) {
                        log.error("Crashing on input: {}", s);
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
            log.info("Processing data [{}] with correlation-id: {}", data, correlationId);
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
                        log.info("Blocking on element: {}", i);
                        Thread.sleep(10); // THIS IS FORBIDDEN ON EVENT LOOP
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return i * 2;
                });
    }

    /**
     * Scenario 5: Checkpointing for labeled debugging.
     */
    public Flux<String> labeledPipeline(Flux<String> input) {
        return input
                .checkpoint("STAGE_1_INPUT")
                .map(String::toUpperCase)
                .checkpoint("STAGE_2_UPPER")
                .<String>handle((s, sink) -> {
                    if (s.contains("FAIL")) {
                        sink.error(new RuntimeException("Manual failure in labeled pipeline"));
                    } else {
                        sink.next(s);
                    }
                })
                .checkpoint("STAGE_3_FINAL");
    }
}
