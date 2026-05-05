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
     * <p>
     * Why is this asynchronous?
     * 1. flatMap provides **Logical Concurrency**: It subscribes to multiple Monos (from userService) 
     *    without waiting for previous ones to complete.
     * 2. delayElement **releases the thread**: It doesn't block. It registers a timer event in the 
     *    Scheduler and frees the thread to do other work.
     * 3. Interleaving: Because of variable latency (10ms vs 100ms), emissions may return 
     *    in a different order than requested (Out-of-order).
     */
    public Flux<String> enrichUserIds(Flux<Integer> ids) {
        // delayElement simulates non-blocking I/O by releasing the thread and scheduling a resume signal
        return ids.flatMap(id ->
                userService.findById(id)
                        .map(user -> "User: " + user)
                        .delayElement(Duration.ofMillis(id % 10 == 0 ? 100 : 10))
        );
    }

    public interface UserService {
        Mono<String> findById(Integer id);
    }
}
