package com.reactivelab.generation;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * Scenario 4: Modern Event Bus using Sinks.
 */
public class NotificationBus {

    // Multicast sink: multiple subscribers receive the same messages
    private final Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

    /**
     * Publishes a message to all active subscribers.
     */
    public void publish(String message) {
        sink.tryEmitNext(message).orThrow();
    }

    /**
     * Returns a Flux that clients can subscribe to.
     */
    public Flux<String> listen() {
        return sink.asFlux();
    }
}
