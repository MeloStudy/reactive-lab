package com.reactivelab.orchestration;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class ResilientClientTest {

    private final ResilientClient client = new ResilientClient();

    @Test
    void shouldReturnDefaultValueOnError() {
        Mono<String> failingCall = Mono.error(new RuntimeException("Boom!"));
        
        StepVerifier.create(client.callWithFallback(failingCall, "Static Default"))
                .expectNext("Static Default")
                .verifyComplete();
    }

    @Test
    void shouldTranslateToBusinessException() {
        Mono<String> failingCall = Mono.error(new IllegalArgumentException("Invalid ID"));

        StepVerifier.create(client.callWithTranslation(failingCall))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(ResilientClient.BusinessException.class);
                    assertThat(error.getMessage()).contains("Service Unavailable");
                })
                .verify();
    }

    @Test
    void shouldFailoverToBackupSource() {
        Mono<String> sourceA = Mono.error(new RuntimeException("Source A Dead"));
        Mono<String> sourceB = Mono.just("Data from Source B");

        StepVerifier.create(client.callWithFailover(sourceA, sourceB))
                .expectNext("Data from Source B")
                .verifyComplete();
    }
}
