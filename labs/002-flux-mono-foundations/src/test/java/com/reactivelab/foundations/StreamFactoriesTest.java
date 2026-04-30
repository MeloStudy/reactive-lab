package com.reactivelab.foundations;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

public class StreamFactoriesTest {

    private final StreamFactories factories = new StreamFactories();

    @Test
    void shouldCreateFluxFromIterable() {
        StepVerifier.create(factories.fromCollection(List.of("A", "B", "C")))
            .expectNext("A", "B", "C")
            .verifyComplete();
    }

    @Test
    void shouldCreateMonoFromCallable() {
        StepVerifier.create(factories.fromDangerousOperation(() -> 42))
            .expectNext(42)
            .verifyComplete();
    }

    @Test
    void shouldCreateEmptyMono() {
        StepVerifier.create(factories.alwaysEmpty())
            .expectNextCount(0)
            .verifyComplete();
    }

    @Test
    void shouldCreateErrorFlux() {
        StepVerifier.create(factories.alwaysError())
            .expectError(IllegalStateException.class)
            .verify();
    }
}
