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
        return Flux.generate(
            () -> new long[]{0L, 1L}, // Initial state: [n-1, n]
            (state, sink) -> {
                long next = state[0] + state[1];
                sink.next(state[0]);
                if (state[0] > 1000000 || count <= 0) { // Safety break or count logic? 
                    // For simplicity, we'll use a count logic in the test via take(n)
                }
                long nPlus1 = state[1];
                state[0] = nPlus1;
                state[1] = next;
                return state;
            }
        ).cast(Long.class).take(count);
    }
}
