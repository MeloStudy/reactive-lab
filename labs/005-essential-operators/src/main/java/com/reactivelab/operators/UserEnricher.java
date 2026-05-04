package com.reactivelab.operators;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Scenario 2: Demonstrates asynchronous flattening with flatMap.
 */
public class UserEnricher {

    private final UserService userService;

    public UserEnricher(UserService userService) {
        this.userService = userService;
    }

    /**
     * Enriches IDs by fetching user details asynchronously.
     * Rule: flatMap allows for concurrency and interleaving of emissions.
     */
    public Flux<String> enrichUserIds(Flux<Integer> ids) {
        return ids.flatMap(id -> userService.findById(id)
                .map(user -> "User: " + user)
                .delayElement(Duration.ofMillis(id % 10 == 0 ? 100 : 10))); // Simulate variable latency
    }

    public interface UserService {
        Mono<String> findById(Integer id);
    }
}
