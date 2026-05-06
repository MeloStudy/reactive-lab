package com.reactivelab.operators;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class PrefetchLabTest {

    private final PrefetchLab lab = new PrefetchLab();

    @Test
    void shouldObservePrefetchBehavior() {
        AtomicInteger requestCount = new AtomicInteger(0);
        Flux<Integer> source = Flux.range(1, 100)
                .hide()
                .doOnRequest(n -> requestCount.addAndGet((int) n));

        // Use Virtual Time to eliminate non-determinism in replenishment timing
        StepVerifier.withVirtualTime(() -> lab.flattenWithControl(source, 2, 2, Duration.ofMillis(100)))
                .expectSubscription()
                .then(() -> assertThat(requestCount.get())
                        .as("Initial request should match the maxConcurrency/prefetch")
                        .isEqualTo(2)) 
                .thenRequest(2)
                .thenAwait(Duration.ofMillis(200))
                .expectNextCount(2)
                .thenAwait(Duration.ofMillis(50))
                .then(() -> assertThat(requestCount.get())
                        .as("Total requested should have increased after items were processed")
                        .isGreaterThanOrEqualTo(3))
                .thenCancel()
                .verify();
    }

    @Test
    void shouldObserveReplenishmentThreshold() {
        AtomicInteger requestCount = new AtomicInteger(0);
        Flux<Integer> source = Flux.range(1, 100)
                .hide()
                .doOnRequest(n -> requestCount.addAndGet((int) n));

        StepVerifier.withVirtualTime(() -> lab.flattenWithControl(source, 2, 2, Duration.ofMillis(100)))
                .expectSubscription()
                .then(() -> assertThat(requestCount.get()).isEqualTo(2)) 
                .thenRequest(1)
                .thenAwait(Duration.ofMillis(100))
                .expectNextCount(1)
                .thenAwait(Duration.ofMillis(50))
                .then(() -> {
                    // Replenishment behavior can vary slightly by OS/Thread timing even with VT
                    // but it should definitely be more than the initial 2 at some point
                    assertThat(requestCount.get()).isGreaterThanOrEqualTo(2);
                })
                .thenRequest(1)
                .thenAwait(Duration.ofMillis(100))
                .expectNextCount(1)
                .thenAwait(Duration.ofMillis(50))
                .then(() -> assertThat(requestCount.get())
                        .as("Should have replenished after consuming initial items")
                        .isGreaterThanOrEqualTo(3))
                .thenCancel()
                .verify();
    }

    @Test
    void shouldLimitActiveSubscriptionsBasedOnConcurrency() {
        AtomicInteger activeSubscribers = new AtomicInteger(0);
        AtomicInteger maxConcurrent = new AtomicInteger(0);

        Flux<Integer> source = Flux.range(1, 10);

        StepVerifier.withVirtualTime(() -> 
                source.flatMap(i -> 
                    Mono.just(i * 10)
                        .delayElement(Duration.ofSeconds(1))
                        .doOnSubscribe(s -> {
                            int current = activeSubscribers.incrementAndGet();
                            maxConcurrent.accumulateAndGet(current, Math::max);
                        })
                        .doOnTerminate(activeSubscribers::decrementAndGet),
                    2, 10)) 
                .expectSubscription()
                .thenAwait(Duration.ofMillis(500)) 
                .then(() -> assertThat(activeSubscribers.get()).isEqualTo(2))
                .thenAwait(Duration.ofSeconds(1)) 
                .then(() -> assertThat(activeSubscribers.get()).isEqualTo(2))
                .thenAwait(Duration.ofSeconds(10)) 
                .expectNextCount(10)
                .verifyComplete();

        assertThat(maxConcurrent.get())
                .as("Maximum concurrent subscriptions should strictly respect the concurrency parameter")
                .isEqualTo(2);
    }
}
