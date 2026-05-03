package com.reactivelab.orchestration;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import java.time.Duration;

public class ActivityAggregatorTest {

    private final ActivityAggregator aggregator = new ActivityAggregator();

    @Test
    void mergeShouldInterleave() {
        // Source 1 is slower (100ms) but emits first item at T=0? No, delayElements(100) delays everything.
        // Let's use a Flux that starts at T=0 but has internal delay
        Flux<String> s1 = Flux.just("S1-1", "S1-2").delayElements(Duration.ofMillis(100));
        Flux<String> s2 = Flux.just("S2-1", "S2-2").delayElements(Duration.ofMillis(10));

        StepVerifier.withVirtualTime(() -> aggregator.aggregateEagerly(s1, s2))
            .expectSubscription()
            .thenAwait(Duration.ofMillis(300))
            // S2 is much faster, it should appear before S1
            .expectNext("S2-1", "S2-2", "S1-1", "S1-2")
            .verifyComplete();
    }

    @Test
    void concatShouldPreserveOrder() {
        Flux<String> s1 = Flux.just("S1-1", "S1-2").delayElements(Duration.ofMillis(100));
        Flux<String> s2 = Flux.just("S2-1", "S2-2").delayElements(Duration.ofMillis(10));

        StepVerifier.withVirtualTime(() -> aggregator.aggregateSequentially(s1, s2))
            .expectSubscription()
            .thenAwait(Duration.ofMillis(300))
            // Concat MUST wait for S1 to finish regardless of S2's speed
            .expectNext("S1-1", "S1-2", "S2-1", "S2-2")
            .verifyComplete();
    }
}
