package com.reactivelab.backpressure;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class QuotaEnforcerTest {

    @Test
    void testEnforceQuota() {
        QuotaEnforcer enforcer = new QuotaEnforcer();
        // Infinite source
        Flux<Long> source = Flux.interval(java.time.Duration.ofMillis(10));
        
        Flux<Long> result = enforcer.enforceQuota(source, 5);

        StepVerifier.withVirtualTime(() -> result)
                .expectSubscription()
                .thenAwait(java.time.Duration.ofMillis(100))
                .expectNext(0L, 1L, 2L, 3L, 4L)
                .verifyComplete();
    }
}
