package com.reactivelab.observability;

import io.micrometer.observation.tck.TestObservationRegistry;
import io.micrometer.observation.tck.TestObservationRegistryAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class Scenario2Test {

    private TestObservationRegistry registry;
    private ObservabilityService observabilityService;

    @BeforeEach
    void setUp() {
        registry = TestObservationRegistry.create();
        // Passing null for WebClient since we only test processCustom here
        observabilityService = new ObservabilityService(null, registry);
    }

    @Test
    void testCustomObservationAndTags() {
        // Given: The service method is invoked with a specific user ID
        String userId = "user-123";

        // When: Subscribing to the custom process
        StepVerifier.create(observabilityService.processCustom(userId))
                // Then: The process emits the expected value
                .expectNext("Processed: user-123")
                .verifyComplete();

        // And Then: TestObservationRegistry validates the custom observation
        TestObservationRegistryAssert.assertThat(registry)
                .hasObservationWithNameEqualTo("custom.process")
                .that()
                .hasLowCardinalityKeyValue("process.type", "user-process")
                .hasHighCardinalityKeyValue("user.id", "user-123");
    }
}
