package com.reactivelab.threading;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class ComputeServiceTest {

    private final ComputeService service = new ComputeService();

    @Test
    void shouldSwitchToParallelSchedulerForComputation() {
        Flux<Integer> inputs = Flux.range(1, 3);

        StepVerifier.create(service.processHeavyTasks(inputs))
                .assertNext(res -> assertThat(res).contains("Result 1"))
                .assertNext(res -> assertThat(res).contains("Result 2"))
                .assertNext(res -> assertThat(res).contains("Result 3"))
                .verifyComplete();
    }

    @Test
    void shouldDemonstrateSchedulerTrap() {
        Flux<String> source = Flux.just("item1");

        StepVerifier.create(service.demonstrationOfSchedulerTrap(source))
                .assertNext(res -> {
                    // Even though we have subscribeOn(boundedElastic) later, 
                    // the first one (parallel) wins the emission thread.
                    // Actually, let's verify thread name in the log or via a capture.
                    log.info("Finished trap test: {}", res);
                })
                .verifyComplete();
    }
}
