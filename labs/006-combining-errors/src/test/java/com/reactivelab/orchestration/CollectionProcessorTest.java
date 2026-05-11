package com.reactivelab.orchestration;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;

class CollectionProcessorTest {

    private final CollectionProcessor processor = new CollectionProcessor();

    @Test
    void shouldCollectIntoMap() {
        Flux<String> source = Flux.just("Apple", "Banana", "Cherry");

        StepVerifier.create(processor.collectIntoMap(source, s -> s.substring(0, 1)))
                .assertNext(map -> {
                    assertThat(map).hasSize(3);
                    assertThat(map.get("A")).isEqualTo("Apple");
                })
                .verifyComplete();
    }

    @Test
    void shouldCollectAndSort() {
        Flux<Integer> source = Flux.just(5, 1, 3);

        StepVerifier.create(processor.collectAndSort(source, Comparator.naturalOrder()))
                .assertNext(list -> assertThat(list).containsExactly(1, 3, 5))
                .verifyComplete();
    }
}
