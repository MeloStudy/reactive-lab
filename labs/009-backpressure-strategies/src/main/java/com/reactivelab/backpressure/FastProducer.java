package com.reactivelab.backpressure;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Flux;

/**
 * A helper class that simulates a "Hot" producer which does not naturally respect pull-based backpressure.
 * It uses Sinks.Many to push data as fast as possible.
 */
@Slf4j
public class FastProducer {

    private final Sinks.Many<Integer> sink;

    public FastProducer() {
        // We use directBestEffort to simulate a producer that pushes signals 
        // regardless of the subscriber's demand, potentially causing overflow.
        this.sink = Sinks.many().multicast().directBestEffort();
    }

    /**
     * Emits a specific number of elements as fast as possible.
     */
    public void emit(int count) {
        log.info("FastProducer emitting {} items...", count);
        for (int i = 1; i <= count; i++) {
            Sinks.EmitResult result = sink.tryEmitNext(i);
            if (result.isFailure()) {
                log.warn("Failed to emit item {}: {}", i, result);
            }
        }
    }

    /**
     * Returns the Flux associated with this producer.
     */
    public Flux<Integer> getFlux() {
        return sink.asFlux()
                .doOnSubscribe(s -> log.info("Subscriber attached to FastProducer"));
    }
}
