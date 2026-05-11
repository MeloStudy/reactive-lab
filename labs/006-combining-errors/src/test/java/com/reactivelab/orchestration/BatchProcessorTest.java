package com.reactivelab.orchestration;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BatchProcessorTest {

    @Test
    void testProcessInBatches() {
        BatchProcessor processor = new BatchProcessor();
        Flux<Integer> source = Flux.range(1, 7); // 1, 2, 3, 4, 5, 6, 7

        Flux<List<Integer>> result = processor.processInBatches(source, 3);

        StepVerifier.create(result)
                .assertNext(batch -> assertThat(batch).containsExactly(1, 2, 3))
                .assertNext(batch -> assertThat(batch).containsExactly(4, 5, 6))
                .assertNext(batch -> assertThat(batch).containsExactly(7))
                .verifyComplete();
    }
}
