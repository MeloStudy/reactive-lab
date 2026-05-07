package com.reactivelab.testing;

import reactor.core.publisher.Flux;
import org.reactivestreams.Publisher;

/**
 * Scenario 2: The Branch Validator
 * Demonstrates how to use PublisherProbe to verify branch execution.
 */
public class BranchValidator {

    /**
     * Executes a primary stream, or falls back to an alternative if empty.
     */
    public Flux<String> processWithFallback(Flux<String> primary, Publisher<String> fallback) {
        return primary.switchIfEmpty(fallback);
    }
}
