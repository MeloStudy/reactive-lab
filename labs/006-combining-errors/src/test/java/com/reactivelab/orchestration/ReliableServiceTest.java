package com.reactivelab.orchestration;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class ReliableServiceTest {

    @Test
    void shouldRetrySpecifiedTimes() {
        ReliableService service = new ReliableService();
        AtomicInteger attempts = new AtomicInteger(0);

        // A source that fails twice then succeeds
        Mono<String> flakySource = Mono.defer(() -> {
            int current = attempts.incrementAndGet();
            if (current <= 2) {
                return Mono.error(new RuntimeException("Transient Error " + current));
            }
            return Mono.just("Success");
        });

        // We use a probe to verify subscription count
        PublisherProbe<String> probe = PublisherProbe.of(flakySource);

        StepVerifier.create(service.callWithRetry(probe.mono(), 3))
                .expectNext("Success")
                .verifyComplete();

        // 1 initial attempt + 2 retries = 3 subscriptions
        probe.assertWasSubscribed();
        assertThat(attempts.get()).isEqualTo(3);
    }
}
