package com.reactivelab.operators;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
}
