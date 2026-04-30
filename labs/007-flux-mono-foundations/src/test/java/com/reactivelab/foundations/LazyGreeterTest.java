package com.reactivelab.foundations;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class LazyGreeterTest {

    @Test
    void shouldNotExecuteSideEffectUntilSubscription() {
        AtomicInteger callCount = new AtomicInteger(0);
        
        LazyGreeter greeter = new LazyGreeter(() -> {
            callCount.incrementAndGet();
            return "Hello Reactive World!";
        });

        // ASSEMBLY TIME
        Mono<String> greetingMono = greeter.getGreeting();

        // ASSERT: No side effect happened yet
        assertThat(callCount.get())
            .as("Side effect should not happen at assembly time")
            .isEqualTo(0);

        // SUBSCRIPTION TIME
        StepVerifier.create(greetingMono)
            .expectNext("Hello Reactive World!")
            .verifyComplete();

        // ASSERT: Side effect happened exactly once after subscription
        assertThat(callCount.get())
            .as("Side effect should happen exactly once after subscription")
            .isEqualTo(1);
    }
}
