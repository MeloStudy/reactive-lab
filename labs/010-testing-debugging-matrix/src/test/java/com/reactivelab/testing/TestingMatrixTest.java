package com.reactivelab.testing;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Hooks;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;
import reactor.test.scheduler.VirtualTimeScheduler;
import reactor.util.context.Context;

import java.time.Duration;

public class TestingMatrixTest {

    @BeforeAll
    static void setup() {
        // BlockHound must be installed before the tests start
        BlockHound.install();
        Hooks.onOperatorDebug();
    }

    @AfterEach
    void tearDown() {
        // Ensure virtual time is reset after each test
        VirtualTimeScheduler.reset();
    }

    @Test
    void scenario1_virtualTime() {
        VirtualTimeScheduler vts = VirtualTimeScheduler.getOrSet();
        Flux<Long> hourlyFlux = Flux.interval(Duration.ofHours(1), vts).take(5);

        StepVerifier.create(hourlyFlux)
                .then(() -> vts.advanceTimeBy(Duration.ofHours(5)))
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    void scenario2_publisherProbe() {
        Mono<String> primary = Mono.empty();
        PublisherProbe<String> probe = PublisherProbe.of(Mono.just("fallback"));

        Mono<String> result = primary.switchIfEmpty(probe.mono());

        StepVerifier.create(result)
                .expectNext("fallback")
                .verifyComplete();

        probe.assertWasSubscribed();
    }

    @Test
    void scenario3_contextPropagation() {
        BuggyService service = new BuggyService();

        Mono<String> pipeline = service.processWithContext("data")
                .contextWrite(Context.of("correlation-id", "TX-12345"));

        StepVerifier.create(pipeline)
                .expectNext("Processed [data] with ID: TX-12345")
                .verifyComplete();
    }

    @Test
    void scenario4_blockHoundDetection() {
        // We use a thread pool that BlockHound monitors
        Flux<Integer> flux = Flux.just(1)
                .subscribeOn(Schedulers.parallel())
                .map(i -> {
                    // This is a blocking call that BlockHound SHOULD detect
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return i;
                });

        StepVerifier.create(flux)
                .expectError(BlockingOperationError.class)
                .verify(Duration.ofSeconds(5));
    }
}
