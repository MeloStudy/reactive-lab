package com.reactivelab.backpressure;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;

/**
 * Visualizes the 75% replenishment rule of the limitRate operator.
 */
public class ReplenishmentTest {

    @Test
    void testLimitRateReplenishment() {
        // We use a probe to track exact request signals sent upstream
        PublisherProbe<Integer> probe = PublisherProbe.of(Flux.range(1, 100));
        
        // Limit rate to 10. Initial request will be 10.
        // Replenishment threshold is 75% of 10 = 7.5 -> usually 8 in Reactor.
        Flux<Integer> throttled = probe.flux().limitRate(10).log("replenish");

        StepVerifier.create(throttled, 0)
                .thenRequest(5) // Consume 5
                .expectNext(1, 2, 3, 4, 5)
                .then(() -> {
                    // Upstream was requested for 10 (initial prefetch)
                    // We have consumed 5, so 5 items are remaining in operator buffer.
                    // Replenishment (at 7.5/8) has NOT triggered yet.
                    probe.assertWasRequested();
                })
                .thenRequest(3) // Consume 3 more. Total consumed: 8.
                .expectNext(6, 7, 8)
                .then(() -> {
                    // Total consumed is 8 (80% of 10).
                    // This is >= 75% threshold, so limitRate should have sent a new request upstream
                    // for the items it just delivered to the downstream.
                    // The request signal should be request(8).
                })
                .thenCancel()
                .verify();
    }
}
