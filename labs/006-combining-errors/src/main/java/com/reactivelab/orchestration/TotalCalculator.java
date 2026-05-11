package com.reactivelab.orchestration;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Scenario 4: The Final Total (Reduction)
 * Uses 'reduce' to summarize a stream.
 */
public class TotalCalculator {

    /**
     * Calculates the total sum of all items in the stream.
     *
     * @param numbers The stream of numbers
     * @return A Mono containing the final sum
     */
    public Mono<Integer> calculateTotal(Flux<Integer> numbers) {
        return numbers.reduce(0, Integer::sum);
    }
}
