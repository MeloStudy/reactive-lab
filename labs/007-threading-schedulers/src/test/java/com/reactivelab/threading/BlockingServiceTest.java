package com.reactivelab.threading;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class BlockingServiceTest {

    private final BlockingService service = new BlockingService();

    @Test
    void shouldRunOnBoundedElasticWithSubscribeOn() {
        StepVerifier.create(service.callBlockingResourceSafely())
                .assertNext(result -> {
                    assertThat(result).contains("boundedElastic");
                    log.info("Verified legacy offloading: {}", result);
                })
                .verifyComplete();
    }

    @Test
    void shouldRunOnVirtualThreadWithSubscribeOn() {
        StepVerifier.create(service.callWithVirtualThreads())
                .assertNext(result -> {
                    // Virtual threads usually have 'VirtualThread' in their name or class
                    assertThat(result).contains("VirtualThread");
                    log.info("Verified modern Loom offloading: {}", result);
                })
                .verifyComplete();
    }
}
