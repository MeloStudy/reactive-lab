package com.reactivelab.observability;

import io.micrometer.observation.tck.TestObservationRegistry;
import io.micrometer.observation.tck.TestObservationRegistryAssert;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import reactor.test.StepVerifier;

import java.io.IOException;

@SpringBootTest
class Scenario1Test {

    private static MockWebServer mockBackEnd;

    @Autowired
    private ObservabilityService observabilityService;

    @Autowired
    private TestObservationRegistry registry;

    @TestConfiguration
    static class ObservationTestConfiguration {
        @Bean
        @Primary
        TestObservationRegistry testObservationRegistry() {
            return TestObservationRegistry.create();
        }
    }

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void clearRegistry() {
        registry.clear();
    }

    @Test
    void testWebClientTracingPropagated() {
        // Given: a mock backend responding with a dummy response
        mockBackEnd.enqueue(new MockResponse()
                .setBody("Mocked response")
                .addHeader("Content-Type", "application/json"));

        String baseUrl = mockBackEnd.url("/mock-endpoint").toString();

        // When: the WebClient is called via ObservabilityService
        StepVerifier.create(observabilityService.callDownstream(baseUrl))
                // Then: it emits the expected mocked response
                .expectNext("Mocked response")
                .verifyComplete();

        // And Then: TestObservationRegistry validates that a client observation was created (tracing propagated)
        TestObservationRegistryAssert.assertThat(registry)
                .doesNotHaveAnyRemainingCurrentObservation()
                .hasObservationWithNameEqualTo("http.client.requests")
                .that()
                .hasLowCardinalityKeyValue("method", "GET")
                .hasLowCardinalityKeyValue("status", "200");
    }
}
