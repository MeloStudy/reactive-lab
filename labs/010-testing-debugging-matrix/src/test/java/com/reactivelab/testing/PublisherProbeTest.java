package com.reactivelab.testing;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;

class PublisherProbeTest {

    @Test
    void testFallbackBranchExecution() {
        BranchValidator validator = new BranchValidator();
        
        // Empty primary stream
        Flux<String> primary = Flux.empty();
        
        // Wrap fallback in a probe to spy on it
        PublisherProbe<String> probe = PublisherProbe.of(Flux.just("Fallback Data"));
        
        Flux<String> result = validator.processWithFallback(primary, probe.flux());

        StepVerifier.create(result)
                .expectNext("Fallback Data")
                .verifyComplete();
        
        // Verify that the fallback was actually subscribed to
        probe.assertWasSubscribed();
    }
}
