package com.reactivelab.context;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.util.context.Context;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class ContextPropagationTest {

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    /**
     * SCENARIO 1: The Correlation ID Journey
     * Goal: Propagate a correlation ID from the Context to the MDC across different threads.
     */
    @Test
    void shouldPropagateCorrelationIdAcrossSchedulers() {
        String correlationIdKey = "correlationId";
        String correlationIdValue = "trace-123";

        Flux<String> pipeline = Flux.just("Event 1", "Event 2")
                .publishOn(Schedulers.parallel())
                .flatMap(event -> Mono.deferContextual(ctx -> {
                    // Manual bridge from Context to MDC
                    String cid = ctx.get(correlationIdKey);
                    try (MDC.MDCCloseable ignored = MDC.putCloseable(correlationIdKey, cid)) {
                        log.info("Processing {} in thread {}", event, Thread.currentThread().getName());
                        // Verify MDC is present during execution
                        assertThat(MDC.get(correlationIdKey)).isEqualTo(correlationIdValue);
                        return Mono.just(event.toUpperCase());
                    }
                }))
                .contextWrite(Context.of(correlationIdKey, correlationIdValue));

        StepVerifier.create(pipeline)
                .expectNext("EVENT 1", "EVENT 2")
                .verifyComplete();
    }

    /**
     * SCENARIO 2: Propagating Security State
     * Goal: Retrieve a complex object (User) from the Context deep in the pipeline.
     */
    @Test
    void shouldRetrieveSecurityUserFromContext() {
        record User(String username, String role) {}
        User mockUser = new User("admin", "SUPER_USER");

        Mono<String> serviceLayer = Mono.deferContextual(ctx -> {
            User user = ctx.get(User.class);
            log.info("Service layer authorized user: {}", user.username());
            return Mono.just("Secure Data for " + user.username());
        });

        Mono<String> pipeline = serviceLayer
                .map(data -> "Result: " + data)
                .contextWrite(Context.of(User.class, mockUser));

        StepVerifier.create(pipeline)
                .expectNext("Result: Secure Data for admin")
                .verifyComplete();
    }
}
