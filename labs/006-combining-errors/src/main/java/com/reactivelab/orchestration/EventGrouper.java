package com.reactivelab.orchestration;

import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;

import java.util.function.Function;

/**
 * Scenario 10: Stream Grouping.
 * Demonstrates the use of 'groupBy' to split a main stream into multiple keyed sub-streams.
 */
public class EventGrouper {

    /**
     * Groups a stream of items by a key.
     * <p>
     * [PEDAGOGICAL NOTE]:
     * groupBy returns a Flux of GroupedFlux. Each GroupedFlux allows you to process
     * items for a specific key independently. This is the reactive version of 'SQL GROUP BY'.
     */
    public <K, T> Flux<GroupedFlux<K, T>> groupEvents(Flux<T> source, Function<T, K> keyExtractor) {
        return source.groupBy(keyExtractor);
    }
}
