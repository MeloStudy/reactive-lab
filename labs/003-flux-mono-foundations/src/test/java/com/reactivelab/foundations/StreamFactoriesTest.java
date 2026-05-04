package com.reactivelab.foundations;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

class StreamFactoriesTest {

    private final StreamFactories factories = new StreamFactories();

    @Test
    void shouldCreateFluxFromIterable() {
        // fromIterable converts a standard Java collection into a Flux.
        StepVerifier.create(factories.fromCollection(List.of("A", "B", "C")))
                .expectNext("A", "B", "C") // Assert all elements arrive in order
                .verifyComplete();         // Assert successful termination
    }

    @Test
    void shouldCreateMonoFromCallable() {
        // fromCallable handles a single item computed lazily.
        StepVerifier.create(factories.fromDangerousOperation(() -> 42))
                .expectNext(42)    // Assert the value is correct
                .verifyComplete(); // Assert completion signal
    }

    @Test
    void shouldCreateEmptyMono() {
        // Mono.empty() emits NO values, only onComplete.
        StepVerifier.create(factories.alwaysEmpty())
                .expectNextCount(0) // Zero items emitted
                .verifyComplete();
    }

    @Test
    void shouldCreateErrorFlux() {
        // .error() immediately emits onError when someone subscribes.
        StepVerifier.create(factories.alwaysError())
                .expectError(IllegalStateException.class) // Assert the specific exception type
                .verify(); // verify() is used instead of verifyComplete() because the stream terminates with error.
    }

    @Test
    void shouldFailOnResubscribingToJavaStream() {
        // Standard Java Streams can only be consumed ONCE. 
        // Reactive Flux created from them inherits this limitation.
        java.util.stream.Stream<String> javaStream = List.of("One").stream();
        reactor.core.publisher.Flux<String> flux = factories.fromJavaStream(javaStream);

        // First subscription works fine
        StepVerifier.create(flux)
                .expectNext("One")
                .verifyComplete();

        // Second subscription MUST fail because the underlying Java stream is closed
        StepVerifier.create(flux)
                .expectError(IllegalStateException.class)
                .verify();
    }

}
