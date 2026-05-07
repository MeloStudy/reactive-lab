package com.reactivelab.generation;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * Scenario 4: The On-Demand Data Stream
 * Demonstrates refCount(n) to manage upstream lifecycle based on demand.
 */
@Slf4j
public class OnDemandResource {

    /**
     * Creates a stream that only starts when 'minSubscribers' arrive,
     * and stops when the last subscriber leaves.
     *
     * @param source         The expensive upstream source
     * @param minSubscribers Minimum subscribers to start
     * @param <T>           The type of data
     * @return A lifecycle-managed Flux
     */
    public <T> Flux<T> getManagedStream(Flux<T> source, int minSubscribers) {
        return source
                .doOnSubscribe(s -> log.info("Demand threshold check: Current subscribers vs requirement ({})", minSubscribers))
                .publish()
                .refCount(minSubscribers)
                .doOnSubscribe(s -> log.info("Subscriber connected to managed stream."))
                .doOnCancel(() -> log.info("Subscriber disconnected from managed stream."));
    }
}
