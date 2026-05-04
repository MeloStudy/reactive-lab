package com.reactivelab.lifecycle;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.publisher.PublisherProbe;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class SubscriptionGroupTest {

    @Test
    void testGroupedCancellation() {
        SubscriptionGroup group = new SubscriptionGroup();

        // Create probes to verify cancellation signals
        PublisherProbe<Long> probe1 = PublisherProbe.of(Flux.interval(Duration.ofMillis(100)));
        PublisherProbe<Long> probe2 = PublisherProbe.of(Flux.interval(Duration.ofMillis(100)));

        group.addSubscriptions(probe1.flux(), probe2.flux());

        assertThat(group.areAllStopped()).isFalse();

        // Stop the entire group
        group.stopAll();

        assertThat(group.areAllStopped()).isTrue();
        probe1.assertWasCancelled();
        probe2.assertWasCancelled();
    }
}
