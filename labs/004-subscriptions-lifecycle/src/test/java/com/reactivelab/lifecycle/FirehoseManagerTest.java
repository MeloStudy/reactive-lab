package com.reactivelab.lifecycle;

import org.junit.jupiter.api.Test;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;
import java.time.Duration;
import static org.assertj.core.api.Assertions.assertThat;

public class FirehoseManagerTest {

    @Test
    public void testManualCancellation() {
        FirehoseManager manager = new FirehoseManager();
        
        // We use virtual time to test the interval without actually waiting
        StepVerifier.withVirtualTime(() -> manager.startFirehose())
            .expectSubscription()
            .thenAwait(Duration.ofMillis(300))
            .expectNext(0L, 1L, 2L)
            .thenCancel() // This is what we are testing: manual cancellation
            .verify();
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
