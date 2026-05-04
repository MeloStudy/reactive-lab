package com.reactivelab.generation;

import reactor.core.publisher.Flux;

/**
 * Scenario 1: Synchronous, state-based sequence generation.
 */
public class SequenceGenerator {

    /**
     * Generates a Fibonacci sequence up to N elements.
     * Rule: Flux.generate is for 1 emission per iteration (Pull model).
     */
    public Flux<Long> generateFibonacci(int count) {
        if (count <= 0) {
            return Flux.empty();
        }

        return Flux.generate(
                () -> new long[]{0L, 1L}, // state[0] = current, state[1] = next
                (state, sink) -> {
                    long current = state[0];
                    long next = state[1];

                    sink.next(current);

                    // 1. Technical safety break (as per original logic but functional)
                    if (current >= 1000000) {
                        sink.complete();
                        return state;
                    }

                    // 2. Overflow protection (Fibonacci grows fast)
                    long sum = current + next;
                    if (sum < 0) { // Simple overflow check for signed long
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
