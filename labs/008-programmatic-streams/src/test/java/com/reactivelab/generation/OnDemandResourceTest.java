package com.reactivelab.generation;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class OnDemandResourceTest {

    @Test
    void shouldOnlySubscribeWhenThresholdMet() {
        OnDemandResource resource = new OnDemandResource();
        PublisherProbe<Integer> probe = PublisherProbe.of(Flux.never());
        
        // Requires 2 subscribers to start
        Flux<Integer> managed = resource.getManagedStream(probe.flux(), 2);

        // Sub 1 arrives
        managed.subscribe();
        probe.assertWasNotSubscribed();

        // Sub 2 arrives -> Threshold met
        managed.subscribe();
        probe.assertWasSubscribed();
        assertThat(probe.subscribeCount()).isEqualTo(1); // Shared subscription
    }

    @Test
    void shouldCancelUpstreamWhenAllSubscribersLeave() {
        OnDemandResource resource = new OnDemandResource();
        // A long running source to test cancellation
        PublisherProbe<Long> probe = PublisherProbe.of(Flux.interval(Duration.ofMillis(100)));
        
        Flux<Long> managed = resource.getManagedStream(probe.flux(), 1);

        // Subscriber 1 joins and then cancels
        StepVerifier.create(managed)
                .expectSubscription()
                .expectNext(0L)
                .thenCancel()
                .verify();

        // Upstream should be cancelled
        probe.assertWasCancelled();
    }
}
