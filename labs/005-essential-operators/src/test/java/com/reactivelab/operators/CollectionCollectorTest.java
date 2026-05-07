package com.reactivelab.operators;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CollectionCollectorTest {

    @Test
    void testCollectToList() {
        CollectionCollector collector = new CollectionCollector();

        Flux<String> source = Flux.just("A", "B", "C");
        Mono<List<String>> result = collector.collectToList(source);

        StepVerifier.create(result)
                .assertNext(list -> {
                    assertThat(list).hasSize(3);
                    assertThat(list).containsExactly("A", "B", "C");
                })
                .verifyComplete();
    }

    @Test
    void testCollectToMap() {
        CollectionCollector collector = new CollectionCollector();
        Flux<String> source = Flux.just("apple", "banana", "cherry");

        // Map by first letter
        Mono<Map<Character, String>> result = collector.collectToMap(
                source, s -> s.charAt(0)
        );

        StepVerifier.create(result)
                .assertNext(map -> {
                    assertThat(map).hasSize(3);
                    assertThat(map).containsEntry('a', "apple");
                    assertThat(map).containsEntry('b', "banana");
                    assertThat(map).containsEntry('c', "cherry");
                })
                .verifyComplete();
    }

    @Test
    void testCollectToSortedList() {
        CollectionCollector collector = new CollectionCollector();
        Flux<Integer> source = Flux.just(5, 1, 3, 2, 4);

        Mono<List<Integer>> result = collector.collectToSortedList(
                source, Comparator.naturalOrder()
        );

        StepVerifier.create(result)
                .assertNext(list -> {
                    assertThat(list).containsExactly(1, 2, 3, 4, 5);
                })
                .verifyComplete();
    }
}
