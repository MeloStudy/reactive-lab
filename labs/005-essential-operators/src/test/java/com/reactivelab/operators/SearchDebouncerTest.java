package com.reactivelab.operators;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

import java.time.Duration;

public class SearchDebouncerTest {

    @Test
    void shouldCancelPreviousSearchWithSwitchMap() {
        SearchDebouncer debouncer = new SearchDebouncer();
        TestPublisher<String> testPublisher = TestPublisher.create();

        // We use virtual time to speed up the test
        StepVerifier.withVirtualTime(() -> debouncer.debounceSearch(testPublisher.flux()))
                .expectSubscription()
                // 1. Emit "A"
                .then(() -> testPublisher.next("A"))
                // 2. Wait 50ms (Search A is pending for T=100)
                .thenAwait(Duration.ofMillis(50))
                // 3. Emit "B" -> This MUST cancel the search for "A"
                .then(() -> testPublisher.next("B"))
                // 4. Complete source
                .then(testPublisher::complete)
                // 5. Wait for B to finish (T=150)
                .thenAwait(Duration.ofMillis(200))
                // Assert: Only B survived
                .expectNext("Result for: B")
                .verifyComplete();
    }
}
