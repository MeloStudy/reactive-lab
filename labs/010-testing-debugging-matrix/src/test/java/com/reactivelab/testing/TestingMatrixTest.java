package com.reactivelab.testing;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Hooks;
import reactor.core.scheduler.Schedulers;
import reactor.core.Scannable;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;
import reactor.util.context.Context;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class TestingMatrixTest {

    @BeforeAll
    static void setup() {
        // Install BlockHound to detect blocking calls on non-blocking threads
        BlockHound.install();
        // Enable assembly-time stack trace capturing
        Hooks.onOperatorDebug();
    }

    @Test
    void scenario1_idiomaticVirtualTime() {
        // Use withVirtualTime with a Supplier to ensure operators see the virtual clock
        StepVerifier.withVirtualTime(() -> 
            Flux.interval(Duration.ofDays(1)).take(365)
        )
        .expectSubscription()
        .expectNoEvent(Duration.ofDays(1)) // Verify nothing happens before 1 day
        .thenAwait(Duration.ofDays(365))  // Warp 1 year
        .expectNextCount(365)
        .verifyComplete();
    }

    @Test
    void scenario2_publisherProbeBranching() {
        Mono<String> primary = Mono.empty();
        PublisherProbe<String> probe = PublisherProbe.of(Mono.just("fallback-data"));

        // Verify that the fallback branch is actually subscribed to
        Mono<String> pipeline = primary.switchIfEmpty(probe.mono());

        StepVerifier.create(pipeline)
                .expectNext("fallback-data")
                .verifyComplete();

        probe.assertWasSubscribed();
    }

    @Test
    void scenario3_multiThreadedContext() {
        BuggyService service = new BuggyService();
        ContextualTracer tracer = new ContextualTracer();

        // Pipeline with multiple thread hops
        Flux<String> pipeline = service.processWithContext("InputData")
                .publishOn(Schedulers.parallel())
                .flatMapMany(s -> tracer.getContextualData("correlation-id"))
                .contextWrite(Context.of("correlation-id", "TX-999"));

        StepVerifier.create(pipeline)
                .expectNext("Value: TX-999")
                .verifyComplete();
    }

    @Test
    void scenario4_blockHoundEnforcement() {
        BuggyService service = new BuggyService();
        
        // This pipeline contains a Thread.sleep() on a parallel thread
        Flux<Integer> blockingFlux = service.blockingPipeline(Flux.just(1))
                .subscribeOn(Schedulers.parallel());

        StepVerifier.create(blockingFlux)
                .expectError(BlockingOperationError.class)
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void scenario5_checkpointLabeling() {
        BuggyService service = new BuggyService();

        // The input "FAIL" will trigger a manual exception in the labeled pipeline
        Flux<String> pipeline = service.labeledPipeline(Flux.just("ok", "FAIL"));

        StepVerifier.create(pipeline)
                .expectNext("OK")
                .expectErrorMatches(throwable -> {
                    // In real logs, you would see "STAGE_2_UPPER" in the stack trace
                    // because the error happens after the upper mapping
                    return throwable.getMessage().contains("Manual failure");
                })
                .verify();
    }

    @Test
    void scenario6_scannableInspection() {
        Flux<Integer> flux = Flux.range(1, 10)
                .name("my-range")
                .tag("category", "testing")
                .filter(i -> i % 2 == 0);

        Scannable scannable = Scannable.from(flux);

        // Verify metadata tags via Scannable
        assertThat(scannable.name()).isEqualTo("my-range");
        assertThat(scannable.tags().collect(java.util.stream.Collectors.toMap(t -> t.getT1(), t -> t.getT2())))
                .containsEntry("category", "testing");

        // Verify that it's NOT terminated yet
        assertThat(scannable.scan(Scannable.Attr.TERMINATED)).isFalse();
    }
}
