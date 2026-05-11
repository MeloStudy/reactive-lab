package com.reactivelab.orchestration;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class TransactionTrackerTest {

    @Test
    void testRunningBalance() {
        TransactionTracker tracker = new TransactionTracker();
        Flux<Double> transactions = Flux.just(100.0, -50.0, 30.0);

        Flux<Double> result = tracker.calculateRunningBalance(10.0, transactions);

        StepVerifier.create(result)
                .expectNext(10.0)  // Initial balance
                .expectNext(110.0) // 10 + 100
                .expectNext(60.0)  // 110 - 50
                .expectNext(90.0)  // 60 + 30
                .verifyComplete();
    }
}
