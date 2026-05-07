package com.reactivelab.generation;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * Scenario 1: Synchronous, state-based sequence generation.
 * Demonstrates the 'pull' model where demand from the subscriber
 * drives exactly one emission per iteration.
 */
@Slf4j
public class SequenceGenerator {

    /**
     * Generates a Fibonacci sequence up to N elements.
     * Rule: Flux.generate is for 1 emission per iteration (Pull model).
     */
    public Flux<Long> generateFibonacci(int count) {
        if (count <= 0) {
            log.warn("Requested Fibonacci sequence with count <= 0. Returning empty flux.");
            return Flux.empty();
        }

        return Flux.generate(
                () -> new long[]{0L, 1L}, // state[0] = current, state[1] = next
                (state, sink) -> {
                    long current = state[0];
                    long next = state[1];

                    log.debug("Generating Fibonacci element: {}", current);
                    sink.next(current);

                    // Technical safety break
                    if (current >= 1000000) {
                        log.info("Reached safety limit for Fibonacci sequence. Completing.");
                        sink.complete();
                        return state;
                    }

                    // Overflow protection (Fibonacci grows fast)
                    long sum = current + next;
                    if (sum < 0) { // Simple overflow check for signed long
                        log.info("Fibonacci overflow detected. Completing sequence.");
                        sink.complete();
                        return state;
                    }

                    state[0] = next;
                    state[1] = sum;
                    return state;
                }
        ).cast(Long.class).take(count);
    }
}
