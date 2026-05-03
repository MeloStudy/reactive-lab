package com.reactivelab.lifecycle;

import reactor.core.publisher.Flux;

/**
 * Scenario 4: Automatic vs. Manual Lifecycle
 * Demonstrates how the 'take' operator automatically handles cancellation.
 */
public class LifecycleComparison {

    /**
     * Returns a stream that will emit at most 'limit' items.
     * The 'take' operator should handle the cancellation of the upstream interval.
     */
    public Flux<Long> getLimitedStream(Flux<Long> source, int limit) {
        return source.take(limit);
    }
}
