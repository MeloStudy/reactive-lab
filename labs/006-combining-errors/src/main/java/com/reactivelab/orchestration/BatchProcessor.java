package com.reactivelab.orchestration;

import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Scenario 5: The Batch Processor
 * Uses 'buffer' to group items for bulk processing.
 */
public class BatchProcessor {

    /**
     * Groups items into batches of the specified size.
     *
     * @param source    The source stream
     * @param batchSize The maximum size of each batch
     * @return A Flux of Lists
     */
    public <T> Flux<List<T>> processInBatches(Flux<T> source, int batchSize) {
        return source.buffer(batchSize);
    }
}
