package com.reactivelab.backpressure;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;

class ThrottledRequesterTest {

    @Test
    void testRateLimitingDemand() {
        ThrottledRequester requester = new ThrottledRequester();
        PublisherProbe<Integer> probe = PublisherProbe.of(Flux.range(1, 100));
        
        // Limit rate to 10
        Flux<Integer> throttled = requester.applyRateLimit(probe.flux(), 10);

        StepVerifier.create(throttled, 0) // Start with 0 demand
                .thenRequest(5)
                .expectNext(1, 2, 3, 4, 5)
                .then(() -> {
                    // Upstream should have been requested for 10 items (the prefetch)
                    // Even though we only asked for 5.
                    probe.assertWasRequested();
                })
                .thenRequest(5)
                .expectNext(6, 7, 8, 9, 10)
                .thenCancel()
                .verify();
    }
}
