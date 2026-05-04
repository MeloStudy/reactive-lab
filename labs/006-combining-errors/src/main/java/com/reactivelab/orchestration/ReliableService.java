package com.reactivelab.orchestration;

import reactor.core.publisher.Mono;

/**
 * Scenario 4: Demonstrates basic transient error recovery with retry.
 */
public class ReliableService {

    /**
     * Retries a flaky call-up to n times.
     */
    public Mono<String> callWithRetry(Mono<String> flakyCall, int retryCount) {
        return flakyCall.retry(retryCount);
    }
}
