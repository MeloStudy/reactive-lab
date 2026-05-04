package com.reactivelab.lifecycle;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;

import java.time.Duration;

public class LifecycleComparisonTest {

    public LifecycleComparisonTest() {
        // Explicit constructor for JUnit instantiation
    }

    @Test
    public void testAutomaticCancellationWithTake() {
        LifecycleComparison comparison = new LifecycleComparison();

        // Use a probe to verify that 'take' cancels the source
        PublisherProbe<Long> probe = PublisherProbe.of(Flux.interval(Duration.ofMillis(100)));

        Flux<Long> limitedStream = comparison.getLimitedStream(probe.flux(), 3);

        StepVerifier.withVirtualTime(() -> limitedStream)
                .expectSubscription()
                .thenAwait(Duration.ofMillis(300))
                .expectNext(0L, 1L, 2L)
                .expectComplete() // take(3) completes after 3 items
                .verify();

        // Critical validation: the operator must have cancelled the upstream interval
        probe.assertWasCancelled();
    }
}
