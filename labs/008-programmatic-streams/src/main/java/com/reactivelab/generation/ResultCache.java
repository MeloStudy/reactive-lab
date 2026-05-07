package com.reactivelab.generation;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Scenario 5: Efficient Resource Sharing via Caching.
 * Demonstrates how to share an expensive upstream source and replay 
 * the last 'n' results to late subscribers.
 */
@Slf4j
public class ResultCache {

    private final AtomicInteger executionCount = new AtomicInteger(0);

    /**
     * Simulates an expensive computation and caches the results.
     *
     * @param historySize Number of items to replay to late subscribers.
     * @return A cached Flux.
     */
    public Flux<String> getCachedCalculation(int historySize) {
        return Flux.just("Result A", "Result B", "Result C", "Result D")
                .doOnSubscribe(s -> {
                    int count = executionCount.incrementAndGet();
                    log.info("Executing expensive calculation (Total executions: {})", count);
                })
                .cache(historySize)
                .doOnSubscribe(s -> log.info("Subscriber joined the cached result stream."));
    }

    public int getExecutionCount() {
        return executionCount.get();
    }
}
