package com.reactivelab.orchestration;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

class WindowProcessorTest {

    @Test
    void testSplitIntoWindows() {
        WindowProcessor processor = new WindowProcessor();
        Flux<Integer> source = Flux.range(1, 10);
        int windowSize = 3;

        // Using flatMap + collectList to simplify the assertion of inner Fluxes
        Flux<List<Integer>> windowedLists = processor.splitIntoWindows(source, windowSize)
                .flatMap(Flux::collectList);

        StepVerifier.create(windowedLists)
                .expectNext(List.of(1, 2, 3))
                .expectNext(List.of(4, 5, 6))
                .expectNext(List.of(7, 8, 9))
                .expectNext(List.of(10))
                .verifyComplete();
    }
}
