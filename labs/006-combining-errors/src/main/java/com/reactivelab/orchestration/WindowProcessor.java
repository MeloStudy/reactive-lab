package com.reactivelab.orchestration;

import reactor.core.publisher.Flux;

/**
 * Scenario 6: The Windowed Stream
 * Demonstrates 'window' to split a stream into sub-streams (Flux of Flux).
 */
public class WindowProcessor {

    /**
     * Splits the source stream into windows of a fixed size.
     * Unlike 'buffer', which produces Lists, 'window' produces nested Fluxes.
     * This allows for more efficient processing as items can be consumed
     * as soon as they arrive in the inner Flux, without waiting for the batch to complete.
     *
     * @param source     The source stream
     * @param windowSize The maximum number of items per window
     * @return A Flux of Fluxes
     */
    public <T> Flux<Flux<T>> splitIntoWindows(Flux<T> source, int windowSize) {
        return source.window(windowSize);
    }
}
