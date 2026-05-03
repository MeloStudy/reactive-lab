package com.reactivelab.foundations;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class ImmutablePipelineTest {

    private final ImmutablePipeline pipeline = new ImmutablePipeline();

    @Test
    void shouldProveFluxIsImmutable() {
        Flux<Integer> source = pipeline.getNumbers();

        // 1. Verify original state: 1, 2, 3, 4, 5
        StepVerifier.create(source)
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();

        // 2. PITFALL: attemptMutation calls map() but doesn't store the result.
        // Because Flux is immutable, the 'source' remains exactly as it was.
        Flux<Integer> ignoredResult = pipeline.attemptMutation(source);

        StepVerifier.create(ignoredResult)
                .expectNext(1, 2, 3, 4, 5) // Still the same! The transformation was lost.
                .verifyComplete();

        // 3. CORRECT: correctTransformation chains the operators or re-assigns.
        Flux<Integer> transformed = pipeline.correctTransformation(source);

        StepVerifier.create(transformed)
                .expectNext(10, 20, 30, 40, 50) // Now we see the * 10 result.
                .verifyComplete();

        // 4. Verify source is STILL unchanged: Immutability ensures safety across different pipelines.
        StepVerifier.create(source)
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();

    }
}
