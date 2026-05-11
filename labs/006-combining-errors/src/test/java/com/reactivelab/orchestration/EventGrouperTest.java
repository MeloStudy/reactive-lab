package com.reactivelab.orchestration;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EventGrouperTest {

    private final EventGrouper grouper = new EventGrouper();

    @Test
    void shouldGroupEventsByFirstLetter() {
        Flux<String> source = Flux.just("apple", "banana", "apricot", "blueberry");

        // We use flatMap + collectList to convert each GroupedFlux into a Mono<List<T>>
        // This is much faster and cleaner for testing than nested StepVerifiers.
        Flux<List<String>> groupedResults = grouper.groupEvents(source, s -> s.substring(0, 1))
                .flatMap(Flux::collectList)
                .sort(Comparator.comparing(List::getFirst));

        StepVerifier.create(groupedResults)
                .assertNext(list -> assertThat(list).containsExactly("apple", "apricot"))
                .assertNext(list -> assertThat(list).containsExactly("banana", "blueberry"))
                .verifyComplete();
    }
}
