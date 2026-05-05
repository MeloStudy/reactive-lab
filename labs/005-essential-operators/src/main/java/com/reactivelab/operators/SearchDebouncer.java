package com.reactivelab.operators;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Scenario 4: Demonstrates "latest-only" flattening with switchMap.
 */
public class SearchDebouncer {

    /**
     * Simulates a debounced search using switchMap.
     *
     * switchMap immediately cancels the previous inner subscription
     * when a new item arrives from the source.
     * Ideal for scenarios where only the latest result matters.
     */
    public Flux<String> debounceSearch(Flux<String> queries) {
        return queries.switchMap(query ->
                Mono.delay(Duration.ofMillis(100)) // Artificial latency
                        .map(l -> "Result for: " + query));
    }
}
