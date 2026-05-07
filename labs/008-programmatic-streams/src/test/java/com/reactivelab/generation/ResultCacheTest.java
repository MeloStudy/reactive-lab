package com.reactivelab.generation;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class ResultCacheTest {

    @Test
    void shouldCacheAndReplayResults() {
        ResultCache cacheService = new ResultCache();
        
        // Request a cached stream with history size of 2
        Flux<String> cachedFlux = cacheService.getCachedCalculation(2);

        // Subscriber 1: Triggers execution
        StepVerifier.create(cachedFlux)
                .expectNext("Result A", "Result B", "Result C", "Result D")
                .verifyComplete();

        assertThat(cacheService.getExecutionCount()).isEqualTo(1);

        // Subscriber 2: Should NOT trigger execution and should get last 2 results instantly
        StepVerifier.create(cachedFlux)
                .expectNext("Result C", "Result D")
                .thenCancel() // We only care about the replay
                .verify();

        // Execution count should still be 1
        assertThat(cacheService.getExecutionCount()).isEqualTo(1);
    }
}
