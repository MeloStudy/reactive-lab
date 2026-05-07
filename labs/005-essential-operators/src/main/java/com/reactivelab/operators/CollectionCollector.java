package com.reactivelab.operators;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Scenario 5: The Collection Collector
 * Demonstrates Flux-to-Mono transitions using various collection operators.
 */
public class CollectionCollector {

    /**
     * Collects all items from a Flux into a single List.
     */
    public <T> Mono<List<T>> collectToList(Flux<T> source) {
        return source.collectList();
    }

    /**
     * Collects all items into a Map using a key selector.
     */
    public <K, T> Mono<Map<K, T>> collectToMap(Flux<T> source, Function<T, K> keySelector) {
        return source.collectMap(keySelector);
    }

    /**
     * Collects and sorts all items into a List.
     */
    public <T> Mono<List<T>> collectToSortedList(Flux<T> source, Comparator<T> comparator) {
        return source.collectSortedList(comparator);
    }
}
