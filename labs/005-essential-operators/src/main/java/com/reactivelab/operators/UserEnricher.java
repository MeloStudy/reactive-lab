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
     *
     * Why is this asynchronous?
     * 1. flatMap subscribes to multiple Monos (from userService) concurrently.
     * 2. delayElement releases the thread, simulating non-blocking I/O.
     * 3. Interleaving: Because of variable latency (10ms vs 100ms),
     *    emissions may return in a different order than requested.
     */
    public Flux<String> enrichUserIds(Flux<Integer> ids) {
        return ids.flatMap(id -> userService.findById(id)
                .map(user -> "User: " + user)
                // delayElement forces a thread switch, proving non-blocking behavior
                .delayElement(Duration.ofMillis(id % 10 == 0 ? 100 : 10)));
    }

    public interface UserService {
        Mono<String> findById(Integer id);
    }
}
