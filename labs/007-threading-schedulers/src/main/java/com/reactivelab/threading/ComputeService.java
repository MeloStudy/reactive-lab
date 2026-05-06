package com.reactivelab.threading;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * Scenario 2: Offloading CPU-intensive tasks using publishOn.
 */
@Slf4j
public class ComputeService {

    /**
     * Performs a heavy computation.
     * We use publishOn to switch to the parallel scheduler for the mapping logic.
     * 
     * [PEDAGOGICAL NOTE]:
     * publishOn influences the execution context of all operators DOWNSTREAM.
     * Use Schedulers.parallel() for tasks that consume CPU cycles (math, parsing).
     */
    public Flux<String> processHeavyTasks(Flux<Integer> inputs) {
        return inputs
                .doOnNext(v -> log.info("Source emission '{}' on thread: {}", v, Thread.currentThread().getName()))
                .publishOn(Schedulers.parallel())
                .map(this::heavyComputation)
                .doOnNext(v -> log.info("Computation result '{}' on thread: {}", v, Thread.currentThread().getName()));
    }

    private String heavyComputation(Integer input) {
        // Simulate high CPU usage
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 50) {
            // Busy wait
        }
        return "Result " + input;
    }

    /**
     * Scenario 4: The Scheduler Trap (Immutable Upstream).
     * 
     * [PEDAGOGICAL NOTE]:
     * Multiple subscribeOn calls do NOT work like publishOn.
     * Only the one closest to the source has effect.
     */
    public Flux<String> demonstrationOfSchedulerTrap(Flux<String> source) {
        return source
                .subscribeOn(Schedulers.parallel()) // This one should win
                .subscribeOn(Schedulers.boundedElastic()) // This one is ignored for emission
                .doOnNext(v -> log.info("Trapped emission '{}' on thread: {}", v, Thread.currentThread().getName()));
    }
}
