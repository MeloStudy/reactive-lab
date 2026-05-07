package com.reactivelab.backpressure;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * Scenario 4: The Hard Stop (Request Limiting)
 * Demonstrates 'limitRequest' to enforce a strict quota on the total emissions.
 */
@Slf4j
public class QuotaEnforcer {

    /**
     * Stops the stream after exactly 'quota' items have been requested.
     *
     * @param source The source Flux
     * @param quota  The maximum number of items to allow
     * @return A Flux that completes after the quota is reached
     */
    public <T> Flux<T> enforceQuota(Flux<T> source, long quota) {
        log.info("Enforcing quota of {} items", quota);
        return source.limitRequest(quota);
    }
}
