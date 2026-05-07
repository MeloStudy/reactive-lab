package com.reactivelab.testing;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

class ContextTest {

    @Test
    void testContextPropagationAcrossThreads() {
        ContextualTracer tracer = new ContextualTracer();
        String key = "traceId";
        String value = "TX-999";

        Flux<String> pipeline = tracer.getContextualData(key)
                .publishOn(Schedulers.parallel()) // Thread hop!
                .map(data -> data + " on " + Thread.currentThread().getName())
                .contextWrite(ctx -> ctx.put(key, value)); // Write context at the bottom

        StepVerifier.create(pipeline)
                .expectNextMatches(s -> s.startsWith("Value: TX-999"))
                .verifyComplete();
    }
}
