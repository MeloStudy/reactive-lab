package com.reactivelab.operators;

import reactor.core.publisher.Flux;

/**
 * Scenario 4: The Data Slicer
 * Demonstrates slicing operators like skip, take, and distinct.
 */
public class DataStreamSlicer {

    /**
     * Slices the stream by skipping initial items, taking a fixed amount,
     * and ensuring uniqueness.
     *
     * @param source The source Flux
     * @param skip   Number of items to skip
     * @param take   Number of items to take
     * @return A sliced and distinct Flux
     */
    public Flux<Integer> sliceAndDistinct(Flux<Integer> source, int skip, int take) {
        return source
                .skip(skip)
                .take(take)
                .distinct();
    }
}
