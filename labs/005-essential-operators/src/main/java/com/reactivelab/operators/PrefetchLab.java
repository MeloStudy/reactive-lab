package com.reactivelab.operators;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Advanced Scenario: Demonstrates how flatMap manages internal buffers (prefetch) and concurrency.
 */
public class PrefetchLab {

    /**
     * Executes flattening with specific concurrency and prefetch controls.
     *
     * @param concurrency The max number of inner publishers subscribed to at once.
     * @param prefetch The number of items to eagerly request from the upstream
     *                 to populate the internal buffer.
     */
    public Flux<Integer> flattenWithControl(Flux<Integer> source, int concurrency, int prefetch) {
        return source.flatMap(i -> Mono.just(i * 10), concurrency, prefetch);
    }

    /**
     * Executes flattening with a delay to simulate real-world asynchronous processing.
     * This makes concurrency behavior much easier to observe.
     */
    public Flux<Integer> flattenWithDelay(Flux<Integer> source, int concurrency, int prefetch, Duration delay) {
        return source.flatMap(i -> Mono.just(i * 10).delayElement(delay), concurrency, prefetch);
    }
}
