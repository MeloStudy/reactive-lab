package com.reactivelab.threading;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class BlockingServiceTest {

    @Test
    void shouldRunOnBoundedElasticWithSubscribeOn() {
        BlockingService service = new BlockingService();

        StepVerifier.create(service.callBlockingResourceSafely())
                .assertNext(result -> {
                    assertThat(result).contains("boundedElastic");
                    log.info("Result: {}", result);
                })
                .verifyComplete();
    }
}
