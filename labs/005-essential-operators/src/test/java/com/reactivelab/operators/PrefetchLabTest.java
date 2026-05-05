package com.reactivelab.operators;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class PrefetchLabTest {

    @Test
    void shouldObservePrefetchBehavior() {
        AtomicInteger requestCount = new AtomicInteger(0);
        Flux<Integer> source = Flux.range(1, 100)
                .doOnRequest(n -> requestCount.addAndGet((int) n));

        // Con concurrency=2 y prefetch=2, pedirá 2 al inicio.
        Flux<Integer> flatMapped = source.flatMap(i -> Mono.just(i * 10).delayElement(Duration.ofMillis(10)), 2, 2);

        StepVerifier.create(flatMapped, 0)
                .expectSubscription()
                .then(() -> assertThat(requestCount.get()).isEqualTo(2)) 
                .thenRequest(2) 
                .expectNextCount(2)
                // Al vaciarse el lote inicial de 2, pide otros 2.
                .then(() -> assertThat(requestCount.get()).isEqualTo(4))
                .thenCancel()
                .verify();
    }

    @Test
    void shouldObserveReplenishmentThreshold() {
        AtomicInteger requestCount = new AtomicInteger(0);
        Flux<Integer> source = Flux.range(1, 100)
                .doOnRequest(n -> requestCount.addAndGet((int) n));

        // Con concurrency=8 y prefetch=8, pedirá 8 al inicio.
        Flux<Integer> flatMapped = source.flatMap(i -> Mono.just(i * 10).delayElement(Duration.ofMillis(10)), 8, 8);

        StepVerifier.create(flatMapped, 0)
                .expectSubscription()
                .then(() -> assertThat(requestCount.get()).isEqualTo(8)) 
                .thenRequest(5).expectNextCount(5)
                .then(() -> assertThat(requestCount.get()).isEqualTo(8))
                .thenRequest(1).expectNextCount(1)
                // Al llegar al 6º (75% de 8), pide 6 más.
                .then(() -> assertThat(requestCount.get()).isEqualTo(14)) 
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

        assertThat(maxConcurrent.get()).isEqualTo(2);
    }
}
