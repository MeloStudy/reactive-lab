package com.reactivelab.foundations;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Demonstrates the critical difference between Eager and Lazy execution in Project Reactor.
 * This test addresses a common pitfall when wrapping imperative code in Mono.
 */
@Slf4j
class EagernessLazinessTest {

    private String expensiveOperation(AtomicInteger counter) {
        log.info("Executing expensive operation...");
        counter.incrementAndGet();
        return "Result";
    }

    @Test
    @DisplayName("Mono.just is EAGER - Argument is evaluated at assembly time")
    void monoJustIsEager() {
        AtomicInteger counter = new AtomicInteger(0);

        log.info("--- Step 1: Assembling Mono.just ---");
        // AT ASSEMBLY TIME: The method is called immediately!
        Mono<String> eagerMono = Mono.just(expensiveOperation(counter));

        assertThat(counter.get())
                .as("Mono.just should have evaluated the operation at assembly time")
                .isEqualTo(1);

        log.info("--- Step 2: Subscribing to Mono.just ---");
        StepVerifier.create(eagerMono)
                .expectNext("Result")
                .verifyComplete();

        assertThat(counter.get())
                .as("Subscription to Mono.just should not re-run the operation")
                .isEqualTo(1);
    }

    @Test
    @DisplayName("Mono.fromCallable is LAZY - Argument is evaluated at subscription time")
    void monoFromCallableIsLazy() {
        AtomicInteger counter = new AtomicInteger(0);

        log.info("--- Step 1: Assembling Mono.fromCallable ---");
        // AT ASSEMBLY TIME: The lambda is stored, but NOT executed.
        Mono<String> lazyMono = Mono.fromCallable(() -> expensiveOperation(counter));

        assertThat(counter.get())
                .as("Mono.fromCallable should NOT have evaluated the operation at assembly time")
                .isZero();

        log.info("--- Step 2: Subscribing to Mono.fromCallable ---");
        StepVerifier.create(lazyMono)
                .expectNext("Result")
                .verifyComplete();

        assertThat(counter.get())
                .as("Subscription to Mono.fromCallable should have triggered the execution")
                .isEqualTo(1);
    }

    @Test
    @DisplayName("Mono.defer is LAZY - Useful for wrapping other publishers lazily")
    void monoDeferIsLazy() {
        AtomicInteger counter = new AtomicInteger(0);

        log.info("--- Step 1: Assembling Mono.defer ---");
        // AT ASSEMBLY TIME: Nothing happens.
        Mono<String> deferredMono = Mono.defer(() -> Mono.just(expensiveOperation(counter)));

        assertThat(counter.get())
                .as("Mono.defer should NOT have evaluated the operation at assembly time")
                .isZero();

        log.info("--- Step 2: Subscribing to Mono.defer ---");
        StepVerifier.create(deferredMono)
                .expectNext("Result")
                .verifyComplete();

        assertThat(counter.get())
                .as("Subscription to Mono.defer should have triggered the execution")
                .isEqualTo(1);
    }
}
