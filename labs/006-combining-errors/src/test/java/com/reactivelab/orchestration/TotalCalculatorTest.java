package com.reactivelab.orchestration;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class TotalCalculatorTest {

    @Test
    void testCalculateTotal() {
        TotalCalculator calculator = new TotalCalculator();
        Flux<Integer> numbers = Flux.just(1, 2, 3, 4, 5);
        
        Mono<Integer> result = calculator.calculateTotal(numbers);

        StepVerifier.create(result)
                .expectNext(15)
                .verifyComplete();
    }
}
