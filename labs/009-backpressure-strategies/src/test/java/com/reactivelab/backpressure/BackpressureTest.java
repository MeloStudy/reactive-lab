package com.reactivelab.backpressure;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public class BackpressureTest {

    @Test
    void scenario1_bufferOperator() {
        // We use a sink that pushes regardless of demand
        Sinks.Many<Integer> sink = Sinks.many().multicast().directBestEffort();
        
        Flux<Integer> bufferedFlux = sink.asFlux()
                .onBackpressureBuffer(10)
                .log("buffer-test");

        StepVerifier.create(bufferedFlux, 0)
                .then(() -> {
                    sink.tryEmitNext(1);
                    sink.tryEmitNext(2);
                })
                .thenRequest(1)
                .expectNext(1)
                .thenRequest(1)
                .expectNext(2)
                .thenCancel()
                .verify();
    }

    @Test
    void scenario2_dropStrategy() {
        Sinks.Many<Integer> sink = Sinks.many().multicast().directBestEffort();
        AtomicInteger droppedCount = new AtomicInteger();
        
        Flux<Integer> droppedFlux = sink.asFlux()
                .onBackpressureDrop(i -> droppedCount.incrementAndGet())
                .log("drop-test");

        StepVerifier.create(droppedFlux, 0)
                .then(() -> {
                    sink.tryEmitNext(1); // Should be dropped
                    sink.tryEmitNext(2); // Should be dropped
                })
                .thenRequest(1)
                .then(() -> sink.tryEmitNext(3)) // Should be captured
                .expectNext(3)
                .then(() -> {
                    if (droppedCount.get() < 2) {
                         throw new AssertionError("Expected 2 dropped elements, got " + droppedCount.get());
                    }
                })
                .thenCancel()
                .verify();
    }

    @Test
    void scenario2_latestStrategy() {
        Sinks.Many<Integer> sink = Sinks.many().multicast().directBestEffort();
        
        Flux<Integer> latestFlux = sink.asFlux()
                .onBackpressureLatest()
                .log("latest-test");

        StepVerifier.create(latestFlux, 0)
                .then(() -> {
                    sink.tryEmitNext(1);
                    sink.tryEmitNext(2);
                })
                .thenRequest(1)
                .expectNext(2) // Latest is 2
                .thenCancel()
                .verify();
    }
}
