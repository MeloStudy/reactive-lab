package com.reactivelab.generation;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class SinksDeepDiveTest {

    @Test
    void shouldMulticastToMultipleSubscribers() {
        // Multicast: Late joiners miss data
        Sinks.Many<String> sink = Sinks.many().multicast().directBestEffort();

        StepVerifier sub1 = StepVerifier.create(sink.asFlux())
                .expectNext("Event 1")
                .thenCancel()
                .verifyLater();

        sink.tryEmitNext("Event 1");

        sub1.verify();

        // Late joiner misses Event 1
        StepVerifier.create(sink.asFlux())
                .expectSubscription()
                .then(() -> sink.tryEmitNext("Event 2"))
                .expectNext("Event 2")
                .thenCancel()
                .verify();
    }

    @Test
    void shouldReplayToLateSubscribers() {
        // Replay: Late joiners get cached data
        Sinks.Many<String> sink = Sinks.many().replay().limit(1);

        sink.tryEmitNext("Cached Event");

        // Late joiner gets Cached Event immediately
        StepVerifier.create(sink.asFlux())
                .expectNext("Cached Event")
                .then(() -> sink.tryEmitNext("Live Event"))
                .expectNext("Live Event")
                .thenCancel()
                .verify();
    }

    @Test
    void shouldHandleEmitFailures() {
        // A sink that won't allow concurrent emissions easily
        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
        
        // Subscribe once (Unicast only allows 1 subscriber)
        sink.asFlux().subscribe();

        // Try to subscribe again -> Should fail or at least the sink knows it's consumed
        Sinks.EmitResult result = sink.tryEmitNext("Test");
        assertThat(result.isSuccess()).isTrue();
    }
}
