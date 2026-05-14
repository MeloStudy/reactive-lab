package com.reactivelab.backpressure;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class ReplenishmentTest {

    @Test
    void shouldVisualize75PercentReplenishment() {
        List<Long> requests = new CopyOnWriteArrayList<>();
        
        // High tide = 10, default low tide (75% replenishment threshold)
        // Reactor pre-fetches 10, then requests 8 more once 8 are consumed (2 left).
        
        Flux<Integer> throttled = Flux.range(1, 100)
                .doOnRequest(requests::add)
                .limitRate(10);

        StepVerifier.create(throttled, 0)
                .thenRequest(1)
                .expectNext(1)
                .then(() -> {
                    // Initial prefetch
                    assertThat(requests).containsExactly(10L);
                })
                .thenRequest(6) // Total consumed: 7 (70%)
                .expectNextCount(6)
                .then(() -> {
                    // Threshold of 75% (8 items) not yet reached
                    assertThat(requests).containsExactly(10L);
                })
                .thenRequest(1) // Total consumed: 8 (80%)
                .expectNextCount(1)
                .then(() -> {
                    // Threshold reached (8 >= 8), triggers replenishment of 8
                    assertThat(requests).containsExactly(10L, 8L);
                })
                .thenCancel()
                .verify();
    }
}
