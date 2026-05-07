package com.reactivelab.operators;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class DataStreamSlicerTest {

    @Test
    void testSliceAndDistinct() {
        DataStreamSlicer slicer = new DataStreamSlicer();
        
        // Input: [0, 1, 2, 3, 3, 4, 5, 5, 6]
        // Skip 2 -> [2, 3, 3, 4, 5, 5, 6]
        // Take 5 -> [2, 3, 3, 4, 5]
        // Distinct -> [2, 3, 4, 5]
        Flux<Integer> source = Flux.just(0, 1, 2, 3, 3, 4, 5, 5, 6);
        
        Flux<Integer> result = slicer.sliceAndDistinct(source, 2, 5);

        StepVerifier.create(result)
                .expectNext(2, 3, 4, 5)
                .verifyComplete();
    }
}
