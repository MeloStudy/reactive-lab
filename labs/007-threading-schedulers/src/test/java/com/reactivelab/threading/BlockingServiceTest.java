package com.reactivelab.threading;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

public class BlockingServiceTest {

    @Test
    void shouldRunOnBoundedElasticWithSubscribeOn() {
        BlockingService service = new BlockingService();

        StepVerifier.create(service.callBlockingResourceSafely())
                .assertNext(result -> {
                    assertThat(result).contains("boundedElastic");
                    System.out.println("Result: " + result);
                })
                .verifyComplete();
    }
}
