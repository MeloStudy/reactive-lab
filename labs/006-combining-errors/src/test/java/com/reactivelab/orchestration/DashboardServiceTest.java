package com.reactivelab.orchestration;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class DashboardServiceTest {

    @Test
    void shouldZipUserAndFriends() {
        DashboardService service = new DashboardService();
        Mono<String> user = Mono.just("melo");
        Mono<Long> friends = Mono.just(500L);

        StepVerifier.create(service.buildHeader(user, friends))
                .expectNext("User: melo | Friends: 500")
                .verifyComplete();
    }

    @Test
    void zipShouldWaitForAllSources() {
        DashboardService service = new DashboardService();
        // Mono.empty() or a delayed source will block the zip from producing
        Mono<String> user = Mono.just("melo");
        Mono<Long> emptyFriends = Mono.empty();

        StepVerifier.create(service.buildHeader(user, emptyFriends))
                .verifyComplete(); // Zip completes immediately if any source completes empty
    }
}
