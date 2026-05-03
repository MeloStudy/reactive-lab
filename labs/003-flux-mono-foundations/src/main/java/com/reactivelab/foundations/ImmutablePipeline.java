package com.reactivelab.foundations;

import reactor.core.publisher.Flux;

/**
 * Demonstrates the Immutability of Project Reactor pipelines.
 * Each operator returns a NEW instance.
 */
public class ImmutablePipeline {

    /**
     * Returns a Flux of numbers.
     */
    public Flux<Integer> getNumbers() {
        return Flux.just(1, 2, 3, 4, 5);
    }

    /**
     * Demonstrates a COMMON MISTAKE: ignoring the return value of an operator.
     * This method will NOT return transformed data if the result of map is ignored.
     */
    public Flux<Integer> attemptMutation(Flux<Integer> source) {
        // This does nothing to 'source' because Flux is immutable
        source.map(n -> n * 10);
        return source;
    }

    /**
     * The CORRECT way: capture the result of the transformation.
     */
    public Flux<Integer> correctTransformation(Flux<Integer> source) {
        return source.map(n -> n * 10);
    }
}
