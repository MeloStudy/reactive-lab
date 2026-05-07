package com.reactivelab.testing;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.time.Duration;

class VirtualTimeTest {

    @Test
    void testHourlyStreamForOneYear() {
        TimeTraveler traveler = new TimeTraveler();
        
        // We warp time to avoid waiting 365 days
        StepVerifier.withVirtualTime(traveler::getHourlyStream)
                .expectSubscription()
                .thenAwait(Duration.ofDays(365))
                .expectNextCount(365 * 24)
                .thenCancel()
                .verify();
    }
}
