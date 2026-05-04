package com.reactivelab.lifecycle;

import org.junit.jupiter.api.Test;
import reactor.core.Disposable;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class FirehoseManagerTest {

    @Test
    public void testManualCancellation() {
        FirehoseManager manager = new FirehoseManager();

        // We use virtual time to test the interval without actually waiting real-world seconds.
        // Rule: virtualTime starts the clock at T=0.
        StepVerifier.withVirtualTime(manager::startFirehose)
                .expectSubscription() // 1. Signal: Subscriber connects to Publisher
                .thenAwait(Duration.ofMillis(300)) // 2. Simulation: Wait for 3 pulses (100ms each)
                .expectNext(0L, 1L, 2L) // 3. Assert: 3 items emitted
                .thenCancel() // 4. Action: Manually send the cancellation signal upstream
                .verify(); // 5. Finalize: verify() triggers the actual subscription
    }

    @Test
    public void testDisposableInterface() {
        FirehoseManager manager = new FirehoseManager();

        // Use a probe to verify cancellation signal was sent upstream
        PublisherProbe<Long> probe = PublisherProbe.of(manager.startFirehose());

        Disposable disposable = manager.subscribeToFirehose(probe.flux());

        assertThat(disposable.isDisposed()).isFalse();

        // Manual cancellation via the Disposable handle
        disposable.dispose();

        assertThat(disposable.isDisposed()).isTrue();
        probe.assertWasCancelled();
    }
}
