package com.reactivelab.orchestration;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Scenario 1: Demonstrates pairing elements from different sources using zip.
 */
@Slf4j
public class DashboardService {

    /**
     * Combines User data and Friends count into a single Header string.
     * 
     * [PEDAGOGICAL NOTE]:
     * Mono.zip (or Flux.zip) waits for ALL sources to emit a signal before
     * producing a combined result. If one source is empty or delayed, the
     * whole zip operation is delayed or results in an empty stream.
     * 
     * Cardinality Rule: The resulting stream is limited by the shortest source.
     */
    public Mono<String> buildHeader(Mono<String> userMono, Mono<Long> friendsMono) {
        return Mono.zip(userMono, friendsMono)
                .map(tuple -> {
                    String user = tuple.getT1();
                    Long count = tuple.getT2();
                    log.debug("Zipping user {} with {} friends", user, count);
                    return "User: " + user + " | Friends: " + count;
                });
    }
}
