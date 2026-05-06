package com.reactivelab.operators;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Advanced Scenario: Demonstrates how flatMap manages internal buffers (prefetch) and concurrency.
 */
public class PrefetchLab {

    /**
     * Executes flattening with specific concurrency, prefetch, and latency controls.
     *
     * @param source The input stream of integers.
     * @param concurrency The max number of inner publishers subscribed to at once.
     * @param prefetch The number of items to eagerly request from the upstream.
     * @param delay The artificial delay to apply to each inner processing (use Duration.ZERO for synchronous).
     * @return A Flux of integers multiplied by 10.
     */
    public Flux<Integer> flattenWithControl(Flux<Integer> source, int concurrency, int prefetch, Duration delay) {
        return source.flatMap(i -> {
            Mono<Integer> result = Mono.just(i * 10);
            if (!delay.isZero()) {
                result = result.delayElement(delay);
            }
            return result;
        }, concurrency, prefetch);
    }
}
