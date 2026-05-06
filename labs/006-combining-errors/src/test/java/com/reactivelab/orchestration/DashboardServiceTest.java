package com.reactivelab.orchestration;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class DashboardServiceTest {

    private final DashboardService service = new DashboardService();

    @Test
    void shouldZipUserAndFriends() {
        Mono<String> user = Mono.just("melo");
        Mono<Long> friends = Mono.just(500L);

        StepVerifier.create(service.buildHeader(user, friends))
                .expectNext("User: melo | Friends: 500")
                .verifyComplete();
    }

    @Test
    void zipShouldCompleteEmptyIfAnySourceIsEmpty() {
        Mono<String> user = Mono.just("melo");
        Mono<Long> emptyFriends = Mono.empty();

        StepVerifier.create(service.buildHeader(user, emptyFriends))
                .verifyComplete(); 
    }
}
