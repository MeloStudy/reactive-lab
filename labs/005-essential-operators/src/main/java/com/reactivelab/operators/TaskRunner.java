package com.reactivelab.operators;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;

/**
 * Scenario 3: Demonstrates sequential asynchronous flattening with concatMap.
 */
public class TaskRunner {

    /**
     * Executes tasks sequentially.
     * Rule: concatMap waits for the previous inner publisher to complete before subscribing to the next.
     */
    public Flux<String> runTasksSequentially(Flux<String> tasks) {
        return tasks.concatMap(task -> Mono.just("Finished: " + task)
            .delayElement(Duration.ofMillis(50))); // Fixed delay to verify order
    }
}
