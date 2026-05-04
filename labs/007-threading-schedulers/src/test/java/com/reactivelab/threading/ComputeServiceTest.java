package com.reactivelab.threading;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class ComputeServiceTest {

    @Test
    void shouldSwitchToParallelWithPublishOn() {
        ComputeService service = new ComputeService();
        Flux<Integer> inputs = Flux.just(1, 2, 3);

        StepVerifier.create(service.processHeavyTasks(inputs))
                .assertNext(result -> assertThat(result).contains("parallel"))
                .assertNext(result -> assertThat(result).contains("parallel"))
                .assertNext(result -> assertThat(result).contains("parallel"))
                .verifyComplete();
    }
}
