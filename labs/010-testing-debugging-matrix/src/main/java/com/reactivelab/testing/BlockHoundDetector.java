package com.reactivelab.testing;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Scenario 4: The BlockHound Sentry
 * Demonstrates how BlockHound catches illegal blocking calls.
 */
public class BlockHoundDetector {

    /**
     * Executes a blocking call inside a reactive flatMap on a parallel scheduler.
     * BlockHound should intercept this.
     */
    public Mono<String> performIllegalBlock() {
        return Mono.just("Start")
                .subscribeOn(Schedulers.parallel())
                .map(s -> {
                    try {
                        Thread.sleep(10); // ILLEGAL BLOCK
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return s + " Done";
                });
    }
}
