package com.reactivelab.operators;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class UserSanitizerTest {

    private final UserSanitizer sanitizer = new UserSanitizer();

    @Test
    void shouldSanitizeAndFilterNames() {
        Flux<String> names = Flux.just("  Alice  ", "bo", "  CHARLIE  ", "", "David");

        StepVerifier.create(sanitizer.sanitizeNames(names))
            .expectNext("alice")   // 1. Signal: Cleaned and lowecased
            .expectNext("charlie") // 2. Signal: "bo" was filtered (<3 chars)
            .expectNext("david")   // 3. Signal: "David" kept its length
            .verifyComplete();     // 4. Assert: Terminal signal
    }
}
