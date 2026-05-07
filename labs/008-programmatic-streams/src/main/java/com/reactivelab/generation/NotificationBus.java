package com.reactivelab.generation;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * Scenario 4: Modern Event Bus using Sinks.
 * Demonstrates manual emission from outside the reactive pipeline
 * using the thread-safe Sinks API.
 */
@Slf4j
public class NotificationBus {

    // Multicast sink: multiple subscribers receive the same messages
    private final Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

    /**
     * Publishes a message to all active subscribers.
     */
    public void publish(String message) {
        log.debug("Emitting notification via Sink: {}", message);
        sink.tryEmitNext(message).orThrow();
    }

    /**
     * Returns a Flux that clients can subscribe to.
     */
    public Flux<String> listen() {
        return sink.asFlux()
                .doOnSubscribe(s -> log.info("New subscriber joined the Notification Bus."));
    }
}
