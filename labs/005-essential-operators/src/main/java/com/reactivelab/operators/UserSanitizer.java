package com.reactivelab.operators;

import reactor.core.publisher.Flux;

/**
 * Scenario 1: Demonstrates synchronous transformations using map and filter.
 */
public class UserSanitizer {

    /**
     * Sanitizes a stream of usernames:
     * 1. Trims whitespace.
     * 2. Converts to lowercase.
     * 3. Filters out empty strings or those with less than 3 characters.
     *
     * Note: map() and filter() are synchronous 1-to-1/1-to-0 transformations.
     * They preserve thread affinity and order.
     */
    public Flux<String> sanitizeNames(Flux<String> input) {
        return input
                .map(String::trim)        // Synchronous transformation
                .map(String::toLowerCase) // Synchronous transformation
                .filter(name -> name.length() >= 3); // Synchronous filtering
    }
}
