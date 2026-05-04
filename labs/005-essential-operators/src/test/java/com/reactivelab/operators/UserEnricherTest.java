package com.reactivelab.operators;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

public class UserEnricherTest {

    @Test
    void shouldInterleaveEmissionsWithFlatMap() {
        // Mock UserService that returns values with different latencies
        UserEnricher.UserService service = id -> Mono.just("U" + id);
        UserEnricher enricher = new UserEnricher(service);

        // We use virtual time to speed up the test
        StepVerifier.withVirtualTime(() -> enricher.enrichUserIds(Flux.just(10, 1))) // 10 will have 100ms delay, 1 will have 10ms delay
                .expectSubscription()
                .thenAwait(Duration.ofMillis(200))
                // Because flatMap interleaves and 1 is faster than 10, 1 should arrive FIRST
                .expectNext("User: U1")
                .expectNext("User: U10")
                .verifyComplete();
    }
}
