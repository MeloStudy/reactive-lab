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
                .doOnRequest(n -> requestCount.addAndGet((int) n));

        // Using a small delay to observe initial prefetch demand
        Flux<Integer> flatMapped = lab.flattenWithControl(source, 2, 2, Duration.ofMillis(10));

        StepVerifier.create(flatMapped, 0)
                .expectSubscription()
                .then(() -> assertThat(requestCount.get())
                        .as("Initial request should match the prefetch amount")
                        .isEqualTo(2)) 
                .thenRequest(1).expectNext(10)
                .thenRequest(1).expectNext(20)
                // Once the buffer is empty, it replenishes (+2)
                .then(() -> assertThat(requestCount.get())
                        .as("Total requested should increase after the prefetch buffer is consumed")
                        .isEqualTo(4))
                .thenCancel()
                .verify();
    }

    @Test
    void shouldObserveReplenishmentThreshold() {
        AtomicInteger requestCount = new AtomicInteger(0);
        Flux<Integer> source = Flux.range(1, 100)
                .doOnRequest(n -> requestCount.addAndGet((int) n));

        // Using small buffer to see exact replenishment
        Flux<Integer> flatMapped = lab.flattenWithControl(source, 2, 2, Duration.ofMillis(10));

        StepVerifier.create(flatMapped, 0)
                .expectSubscription()
                .then(() -> assertThat(requestCount.get()).isEqualTo(2)) 
                .thenRequest(1).expectNextCount(1)
                .then(() -> assertThat(requestCount.get()).isEqualTo(2)) // Not replenished yet
                .thenRequest(1).expectNextCount(1)
                .then(() -> assertThat(requestCount.get()).isEqualTo(4)) // Replenished after 2 items
                .thenCancel()
                .verify();
    }

    @Test
    void shouldLimitActiveSubscriptionsBasedOnConcurrency() {
        AtomicInteger activeSubscribers = new AtomicInteger(0);
        AtomicInteger maxConcurrent = new AtomicInteger(0);

        Flux<Integer> source = Flux.range(1, 10);

        // We use instrumentation on the inner publisher logic to track concurrency
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
