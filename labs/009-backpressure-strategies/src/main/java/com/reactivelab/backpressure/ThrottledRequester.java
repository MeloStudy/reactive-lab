package com.reactivelab.backpressure;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * Scenario 3: The Polite Subscriber (Rate Limiting)
 * Demonstrates 'limitRate' to control upstream demand.
 */
@Slf4j
public class ThrottledRequester {

    /**
     * Limits the prefetch rate to the specified amount.
     *
     * @param source   The fast upstream source
     * @param rate     The prefetch rate
     * @return A rate-limited Flux
     */
    public <T> Flux<T> applyRateLimit(Flux<T> source, int rate) {
        log.info("Applying rate limit: {} items", rate);
        return source.limitRate(rate)
                .doOnRequest(n -> log.info("Downstream requested: {}", n));
    }
}
