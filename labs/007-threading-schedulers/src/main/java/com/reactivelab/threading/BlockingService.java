package com.reactivelab.threading;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Scenario 1: Isolating blocking I/O using subscribeOn.
 */
public class BlockingService {

    /**
     * Executes a blocking call. 
     * Without subscribeOn, this would freeze the calling thread (e.g. the Event Loop).
     */
    public Mono<String> callBlockingResource() {
        return Mono.fromCallable(() -> {
            // Simulate blocking I/O
            Thread.sleep(1000);
            return "Data from Slow Resource on " + Thread.currentThread().getName();
        });
    }

    /**
     * Rescues the blocking call by moving it to the boundedElastic scheduler.
     */
    public Mono<String> callBlockingResourceSafely() {
        return callBlockingResource()
            .subscribeOn(Schedulers.boundedElastic());
    }
}
