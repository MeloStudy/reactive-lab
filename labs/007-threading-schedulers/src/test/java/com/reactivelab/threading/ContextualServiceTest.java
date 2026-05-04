package com.reactivelab.threading;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class ContextualServiceTest {

    @Test
    void shouldPropagateContextAcrossThreads() {
        ContextualService service = new ContextualService();
        String testId = "TX-12345";

        StepVerifier.create(service.runWithContext(testId))
                .assertNext(result -> {
                    assertThat(result).contains(testId);
                    assertThat(result).contains("parallel");
                    System.out.println("Result: " + result);
                })
                .verifyComplete();
    }

    @Test
    void shouldFailIfContextIsMissing() {
        ContextualService service = new ContextualService();

        StepVerifier.create(service.getCorrelationIdAfterHop())
                .assertNext(result -> assertThat(result).contains("NOT_FOUND"))
                .verifyComplete();
    }
}
