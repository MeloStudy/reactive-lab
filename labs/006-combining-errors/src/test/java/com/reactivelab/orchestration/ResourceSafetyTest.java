package com.reactivelab.orchestration;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ResourceSafetyTest {

    @Test
    void testDiscardSupportOnFilter() {
        List<Integer> discardedItems = new ArrayList<>();

        Flux<Integer> flux = Flux.just(1, 2, 3, 10, 20)
                .filter(i -> i > 5)
                .doOnDiscard(Integer.class, discardedItems::add);

        StepVerifier.create(flux)
                .expectNext(10, 20)
                .verifyComplete();

        // Items 1, 2, and 3 were rejected by filter, so they should be in the discarded list
        assertThat(discardedItems).containsExactlyInAnyOrder(1, 2, 3);
    }

    @Test
    void testDiscardSupportOnCancellation() {
        List<Integer> discardedItems = new ArrayList<>();

        // We use a sink to manually control emission and trigger cancellation while buffering
        Sinks.Many<Integer> sink = Sinks.many().unicast().onBackpressureBuffer();

        Flux<List<Integer>> flux = sink.asFlux()
                .buffer(5)
                .doOnDiscard(Integer.class, discardedItems::add);

        StepVerifier.create(flux)
                .then(() -> {
                    sink.tryEmitNext(1);
                    sink.tryEmitNext(2);
                })
                .thenCancel()
                .verify();

        // The items 1 and 2 were in the buffer when the stream was cancelled.
        // They should be caught by doOnDiscard.
        assertThat(discardedItems).containsExactlyInAnyOrder(1, 2);
    }
}
