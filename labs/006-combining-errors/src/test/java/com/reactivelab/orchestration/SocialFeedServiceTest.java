package com.reactivelab.orchestration;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

class SocialFeedServiceTest {

    private final SocialFeedService service = new SocialFeedService();

    @Test
    void mergeShouldInterleaveItemsFromBothSources() {
        // Source 1 emits at 100ms, 200ms
        Flux<String> twitter = Flux.just("Tweet 1", "Tweet 2")
                .delayElements(Duration.ofMillis(100));
        
        // Source 2 emits at 10ms, 20ms
        Flux<String> instagram = Flux.just("Insta 1", "Insta 2")
                .delayElements(Duration.ofMillis(10));

        // Using VirtualTime to simulate time passing and verify interleaving
        StepVerifier.withVirtualTime(() -> service.combineFeedsEagerly(twitter, instagram))
                .expectSubscription()
                .thenAwait(Duration.ofMillis(300))
                // Instagram items arrive first because they are faster
                .expectNext("Insta 1", "Insta 2", "Tweet 1", "Tweet 2")
                .verifyComplete();
    }

    @Test
    void concatShouldPreserveOrderRegardlessOfSpeed() {
        // Source 1 is SLOW (100ms)
        Flux<String> cache = Flux.just("Cache 1", "Cache 2")
                .delayElements(Duration.ofMillis(100));
        
        // Source 2 is FAST (10ms)
        Flux<String> remote = Flux.just("Remote 1", "Remote 2")
                .delayElements(Duration.ofMillis(10));

        StepVerifier.withVirtualTime(() -> service.combineFeedsSequentially(cache, remote).log("sequential-feed"))
                .expectSubscription()
                .thenAwait(Duration.ofMillis(500))
                // Cache MUST complete before Remote starts, even if Remote is faster
                .expectNext("Cache 1", "Cache 2", "Remote 1", "Remote 2")
                .verifyComplete();
    }
}
