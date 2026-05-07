package com.reactivelab.testing;

import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * Scenario 1: The Time Traveler
 * A stream that emits events over a very long period.
 */
public class TimeTraveler {

    /**
     * Emits an item every hour.
     */
    public Flux<String> getHourlyStream() {
        return Flux.interval(Duration.ofHours(1))
                .map(i -> "Hour " + (i + 1));
    }
}
