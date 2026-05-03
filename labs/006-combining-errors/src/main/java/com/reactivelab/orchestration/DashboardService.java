package com.reactivelab.orchestration;

import reactor.core.publisher.Mono;

/**
 * Scenario 1: Demonstrates pairing elements from different sources using zip.
 */
public class DashboardService {

    /**
     * Combines User data and Friends count into a single Header string.
     * Rule: zip waits for ALL sources to emit before producing a pair.
     */
    public Mono<String> buildHeader(Mono<String> userMono, Mono<Long> friendsMono) {
        return Mono.zip(userMono, friendsMono)
            .map(tuple -> "User: " + tuple.getT1() + " | Friends: " + tuple.getT2());
    }
}
