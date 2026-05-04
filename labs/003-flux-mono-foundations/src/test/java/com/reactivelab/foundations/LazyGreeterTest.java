package com.reactivelab.foundations;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class LazyGreeterTest {

    @Test
    void shouldNotExecuteSideEffectUntilSubscription() {
        AtomicInteger callCount = new AtomicInteger(0);

        LazyGreeter greeter = new LazyGreeter(() -> {
            callCount.incrementAndGet();
            return "Hello Reactive World!";
        });

        // ASSEMBLY TIME: The greetingMono is created, but the lambda hasn't run.
        Mono<String> greetingMono = greeter.getGreeting();

        // ASSERT: Prove the "Lazy" nature. Assembly != Execution.
        assertThat(callCount.get())
                .as("Side effect should not happen at assembly time")
                .isZero();

        // SUBSCRIPTION TIME: This is what triggers the upstream demand.
        StepVerifier.create(greetingMono)
                .expectNext("Hello Reactive World!") // Expect the result of the callable
                .verifyComplete();                 // Call verify() to actually subscribe and start the flow

        // ASSERT: Now that subscription happened, the counter must be 1.
        assertThat(callCount.get())
                .as("Side effect should happen exactly once after subscription")
                .isEqualTo(1);

    }
}
