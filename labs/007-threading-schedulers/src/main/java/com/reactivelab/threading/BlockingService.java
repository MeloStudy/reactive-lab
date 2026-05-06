package com.reactivelab.threading;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;

/**
 * Scenario 1: Isolating blocking I/O using subscribeOn.
 * 
 * [PEDAGOGICAL NOTE]:
 * Blocking operations in a reactive pipeline are a "sin" because they tie up
 * precious threads. If you block an Event Loop thread, the whole app stalls.
 */
@Slf4j
public class BlockingService {

    /**
     * Executes a blocking call.
     * Without subscribeOn, this would freeze the calling thread.
     */
    public Mono<String> callBlockingResource() {
        return Mono.fromCallable(() -> {
            log.info("Starting blocking task on thread: {}", Thread.currentThread());
            // Simulate blocking I/O
            Thread.sleep(100);
            // Use toString() to capture VirtualThread status even if name is null
            return "Data from Slow Resource on " + Thread.currentThread().toString();
        });
    }

    /**
     * Traditional Solution: offload to boundedElastic scheduler.
     */
    public Mono<String> callBlockingResourceSafely() {
        return callBlockingResource()
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Modern Solution (Java 21+): offload to Virtual Threads.
     */
    public Mono<String> callWithVirtualThreads() {
        return callBlockingResource()
                .subscribeOn(Schedulers.fromExecutor(Executors.newVirtualThreadPerTaskExecutor()));
    }
}
