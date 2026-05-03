package com.reactivelab.orchestration;

import reactor.core.publisher.Flux;

/**
 * Scenario 2: Demonstrates the difference between merge (interleaved) and concat (sequential).
 */
public class ActivityAggregator {

    /**
     * Merges activities from two sources as fast as possible.
     * Rule: merge is eager and interleaves emissions.
     */
    public Flux<String> aggregateEagerly(Flux<String> source1, Flux<String> source2) {
        return Flux.merge(source1, source2);
    }

    /**
     * Concatenates activities from two sources in order.
     * Rule: concat waits for source1 to complete before subscribing to source2.
     */
    public Flux<String> aggregateSequentially(Flux<String> source1, Flux<String> source2) {
        return Flux.concat(source1, source2);
    }
}
