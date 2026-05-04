package com.reactivelab.lifecycle;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LifecycleTrackerTest {

    @Test
    void testLifecycleHooksExecutionOrder() {
        LifecycleTracker tracker = new LifecycleTracker();
        List<String> eventLog = new ArrayList<>();

        Flux<String> source = Flux.just("A", "B");
        Flux<String> tracked = tracker.trackLifecycle(source, eventLog);

        StepVerifier.create(tracked)
                .expectNext("A", "B")
                .expectComplete()
                .verify();

        // Verify the order of events
        assertThat(eventLog).containsExactly(
                "SUBSCRIBE",
                "NEXT:A",
                "NEXT:B",
                "COMPLETE",
                "FINALLY:onComplete"
        );
    }

    @Test
    void testLifecycleHooksOnCancel() {
        LifecycleTracker tracker = new LifecycleTracker();
        List<String> eventLog = new ArrayList<>();

        // Use a stream that never completes to force a cancellation
        Flux<String> source = Flux.never();
        Flux<String> tracked = tracker.trackLifecycle(source, eventLog);

        StepVerifier.create(tracked)
                .expectSubscription()
                .thenCancel()
                .verify();

        assertThat(eventLog).containsExactly(
                "SUBSCRIBE",
                "CANCEL",
                "FINALLY:cancel"
        );
    }
}
