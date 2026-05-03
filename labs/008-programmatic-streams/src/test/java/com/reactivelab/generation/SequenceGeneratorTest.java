package com.reactivelab.generation;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

public class SequenceGeneratorTest {

    @Test
    void shouldGenerateFibonacciSequence() {
        SequenceGenerator generator = new SequenceGenerator();

        StepVerifier.create(generator.generateFibonacci(5))
            .expectNext(0L)
            .expectNext(1L)
            .expectNext(1L)
            .expectNext(2L)
            .expectNext(3L)
            .verifyComplete();
    }
}
