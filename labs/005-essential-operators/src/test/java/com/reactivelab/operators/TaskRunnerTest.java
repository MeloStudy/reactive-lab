package com.reactivelab.operators;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class TaskRunnerTest {

    @Test
    void shouldMaintainSequentialOrderWithConcatMap() {
        TaskRunner runner = new TaskRunner();

        // Even with delays, concatMap MUST maintain input order
        StepVerifier.withVirtualTime(() -> runner.runTasksSequentially(Flux.just("A", "B", "C")))
                .expectSubscription()
                .thenAwait(Duration.ofMillis(300))
                .expectNext("Finished: A")
                .expectNext("Finished: B")
                .expectNext("Finished: C")
                .verifyComplete();
    }
}
