package com.reactivelab.threading;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * Scenario 2: Offloading CPU-intensive tasks using publishOn.
 */
public class ComputeService {

    /**
     * Performs a heavy computation.
     * We use publishOn to switch to the parallel scheduler for the mapping logic.
     */
    public Flux<String> processHeavyTasks(Flux<Integer> inputs) {
        return inputs
            .publishOn(Schedulers.parallel())
            .map(this::heavyComputation);
    }

    private String heavyComputation(Integer input) {
        // Simulate high CPU usage
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 100) {
            // Busy wait
        }
        return "Result " + input + " on " + Thread.currentThread().getName();
    }
}
