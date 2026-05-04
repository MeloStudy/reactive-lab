package com.reactivelab.orchestration;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class ResilientClientTest {

    private final ResilientClient client = new ResilientClient();

    @Test
    void shouldFallbackToDefaultValue() {
        Mono<String> failingCall = Mono.error(new RuntimeException("Boom"));

        StepVerifier.create(client.callWithFallback(failingCall, "FallbackValue"))
                .expectNext("FallbackValue")
                .verifyComplete();
    }

    @Test
    void shouldTranslateException() {
        Mono<String> failingCall = Mono.error(new RuntimeException("Boom"));

        StepVerifier.create(client.callWithTranslation(failingCall))
                .expectErrorMatches(throwable ->
                        throwable instanceof ResilientClient.BusinessException &&
                                throwable.getMessage().contains("Service Translation: Boom") &&
                                throwable.getCause() instanceof RuntimeException
                )
                .verify();
    }

    @Test
    void shouldFailoverToSourceB() {
        Mono<String> sourceA = Mono.error(new RuntimeException("Source A Dead"));
        Mono<String> sourceB = Mono.just("Data from Source B");

        StepVerifier.create(client.callWithFailover(sourceA, sourceB))
                .expectNext("Data from Source B")
                .verifyComplete();
    }
}
