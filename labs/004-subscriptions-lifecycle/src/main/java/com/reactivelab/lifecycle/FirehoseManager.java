package com.reactivelab.lifecycle;

import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * Scenario 1: The Controlled Firehose
 * Demonstrates manual cancellation of a long-running Flux using the Disposable interface.
 */
@Slf4j
public class FirehoseManager {

    /**
     * Starts an infinite stream of items.
     *
     * @return A Flux that emits Long values every 100ms.
     */
    public Flux<Long> startFirehose() {
        return Flux.interval(Duration.ofMillis(100));
    }

    /**
     * Subscribes to a stream and returns the Disposable handle.
     * The caller can use this handle to stop the stream manually.
     *
     * @param flux The stream to subscribe to.
     * @return A Disposable representing the active subscription.
     */
    public Disposable subscribeToFirehose(Flux<Long> flux) {
        // subscribe() returns a Disposable
        return flux.subscribe(
                item -> log.info("Received: {}", item),
                error -> log.error("Error: {}", error.getMessage(), error),
                () -> log.info("Stream Completed")
        );
    }
}
