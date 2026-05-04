package com.reactivelab.operators;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicInteger;

class PrefetchLabTest {

    @Test
    void shouldObservePrefetchBehavior() {
        PrefetchLab lab = new PrefetchLab();
        AtomicInteger requestCount = new AtomicInteger(0);

        // A source that tracks how many items were requested
        Flux<Integer> source = Flux.range(1, 100)
                .doOnRequest(n -> requestCount.addAndGet((int) n));

        // Use a low prefetch of 2
        StepVerifier.create(lab.flattenWithControl(source, 1, 2))
                .expectSubscription()
                .thenRequest(1) // Request 1 from flatMap
                .expectNext(10)
                .thenRequest(1)
                .expectNext(20)
                .thenCancel()
                .verify();

        // Even though we only requested 2 items from the StepVerifier, 
        // flatMap with prefetch=2 should have requested 2 from the source initially.
        // Actually, flatMap prefetch logic is a bit more complex, 
        // but it should definitely be >= 2.
        assert requestCount.get() >= 2;
    }
}
