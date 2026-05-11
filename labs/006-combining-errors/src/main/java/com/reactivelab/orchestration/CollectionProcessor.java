package com.reactivelab.orchestration;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Scenario 9: Advanced Collections.
 * Demonstrates transforming a stream into complex Java structures like Maps and sorted Lists.
 */
public class CollectionProcessor {

    /**
     * Collects a stream of items into a Map using a key extractor.
     * <p>
     * [PEDAGOGICAL NOTE]:
     * collectMap reduces a Flux into a Mono<Map>. It is essential for building
     * lookups or caching results from a stream once it completes.
     */
    public <K, V> Mono<Map<K, V>> collectIntoMap(Flux<V> source, Function<V, K> keyExtractor) {
        return source.collectMap(keyExtractor);
    }

    /**
     * Collects all items into a List and sorts them.
     * <p>
     * [PEDAGOGICAL NOTE]:
     * collectSortedList is more efficient than collectList().map(list -> sort(list))
     * as it handles the sorting as part of the terminal aggregation.
     */
    public <T> Mono<List<T>> collectAndSort(Flux<T> source, Comparator<T> comparator) {
        return source.collectSortedList(comparator);
    }
}
