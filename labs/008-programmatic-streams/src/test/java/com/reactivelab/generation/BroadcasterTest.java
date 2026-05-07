package com.reactivelab.generation;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.time.Duration;

class BroadcasterTest {

    @Test
    void testColdStreamRestarts() {
        Broadcaster broadcaster = new Broadcaster();
        Flux<String> cold = broadcaster.getColdStream();

        // First subscriber hears all
        StepVerifier.withVirtualTime(() -> cold)
                .expectSubscription()
                .thenAwait(Duration.ofMillis(300))
                .expectNext("Line 1", "Line 2", "Line 3")
                .verifyComplete();

        // Second subscriber also hears all
        StepVerifier.withVirtualTime(() -> cold)
                .expectSubscription()
                .thenAwait(Duration.ofMillis(300))
                .expectNext("Line 1", "Line 2", "Line 3")
                .verifyComplete();
    }

    @Test
    void testHotStreamMissesData() {
        // We use Sinks to represent a live source (Hot)
        Sinks.Many<String> sink = Sinks.many().multicast().directBestEffort();
        Flux<String> hot = sink.asFlux();

        // Emit first item BEFORE any subscriber exists
        sink.tryEmitNext("Live 1");

        StepVerifier.create(hot)
                .expectSubscription()
                .then(() -> {
                    // Emit subsequent items
                    sink.tryEmitNext("Live 2");
                    sink.tryEmitComplete();
                })
                .expectNext("Live 2") // Missed Live 1
                .verifyComplete();
    }
}
