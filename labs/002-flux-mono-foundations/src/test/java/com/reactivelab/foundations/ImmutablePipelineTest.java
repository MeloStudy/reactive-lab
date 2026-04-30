package com.reactivelab.foundations;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class ImmutablePipelineTest {

    private final ImmutablePipeline pipeline = new ImmutablePipeline();

    @Test
    void shouldProveFluxIsImmutable() {
        Flux<Integer> source = pipeline.getNumbers();

        // 1. Verify original state
        StepVerifier.create(source)
            .expectNext(1, 2, 3, 4, 5)
            .verifyComplete();

        // 2. Demonstrate that ignoring return value doesn't change original
        Flux<Integer> ignoredResult = pipeline.attemptMutation(source);
        
        StepVerifier.create(ignoredResult)
            .expectNext(1, 2, 3, 4, 5) // Still the same!
            .verifyComplete();

        // 3. Demonstrate correct transformation
        Flux<Integer> transformed = pipeline.correctTransformation(source);

        StepVerifier.create(transformed)
            .expectNext(10, 20, 30, 40, 50)
            .verifyComplete();
            
        // 4. Verify source is STILL unchanged even after correct transformation of a derived stream
        StepVerifier.create(source)
            .expectNext(1, 2, 3, 4, 5)
            .verifyComplete();
    }
}
