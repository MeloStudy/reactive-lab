package com.reactivelab.resilience;

import com.reactivelab.resilience.model.Item;
import com.reactivelab.resilience.service.ResilienceService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ResilienceIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ResilienceService resilienceService;

    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws IOException {
        // Initialize the MockWebServer to mock downstream services for Scenario 6
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        // Shutdown the MockWebServer to release ports and resources cleanly
        mockWebServer.shutdown();
    }

    @Test
    void scenario1_LocalFallback() {
        // GIVEN: A failure-inducing request to the fallback endpoint "/api/resilience/items/fail"
        // WHEN: Performing a GET exchange
        // THEN: Verify that the endpoint returns 200 OK and intercepts the error to emit a safe fallback JSON object
        webTestClient.get()
                .uri("/api/resilience/items/fail")
                .exchange()
                // Assert that the HTTP Status is 200 OK rather than a 5xx error
                .expectStatus().isOk()
                // Assert that the response JSON contains the default item properties
                .expectBody()
                // Validate that the id is mapped to the safe fallback "0"
                .jsonPath("$.id").isEqualTo("0")
                // Validate that the name is mapped to the safe fallback "Default Item"
                .jsonPath("$.name").isEqualTo("Default Item");
    }

    @Test
    void scenario2_ControllerExceptionHandler() {
        // GIVEN: An invalid request for an unknown product product ID "/api/resilience/products/unknown"
        // WHEN: Requesting the endpoint
        // THEN: Verify the controller's @ExceptionHandler intercepts ProductNotFoundException and returns 404 NOT FOUND
        webTestClient.get()
                .uri("/api/resilience/products/unknown")
                .exchange()
                // Assert that the local handler correctly sets status to 404 NOT FOUND
                .expectStatus().isNotFound()
                // Assert that the returned plain text matches the custom ProductNotFoundException message
                .expectBody(String.class).isEqualTo("Product with ID unknown not found");
    }

    @Test
    void scenario3_GlobalHandler_ProblemDetail() {
        // GIVEN: A secure endpoint that throws a RuntimeException when the X-Security-Key header is missing
        // WHEN: Invoking the secure route without any header
        // THEN: Verify the GlobalErrorWebExceptionHandler intercepts it and renders an RFC 7807 problem detail response
        webTestClient.get()
                .uri("/api/resilience/secure") 
                .exchange()
                // Assert that the unhandled exception propagates up and results in a 500 Internal Server Error
                .expectStatus().is5xxServerError()
                // Assert that the content-type strictly conforms to the RFC 7807 standard (application/problem+json)
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                // Assert standard ProblemDetail JSON schema elements
                .jsonPath("$.title").isEqualTo("Reactive Lab Error")
                .jsonPath("$.status").isEqualTo(500)
                .jsonPath("$.detail").isEqualTo("Missing security key");
    }

    @Test
    void scenario4_ContextPropagation_CorrelationId() {
        // GIVEN: A request containing a specific correlation trace header "test-trace-123"
        // WHEN: Making the request, triggering a downstream exception
        // THEN: Verify the correlation ID propagates through the Reactor Context and appears in the global error response
        webTestClient.get()
                .uri("/api/resilience/secure")
                .header("X-Correlation-ID", "test-trace-123")
                .exchange()
                // Assert status is 500
                .expectStatus().is5xxServerError()
                .expectBody()
                // Assert that the global handler successfully pulled "test-trace-123" from Reactor Context
                .jsonPath("$.correlation_id").isEqualTo("test-trace-123");
    }

    @Test
    void scenario5_ServerSideTimeout() {
        // GIVEN: An endpoint "/api/resilience/slow" that triggers a sluggish service delaying for 5 seconds
        // WHEN: Performing a GET exchange
        // THEN: Verify that the controller-level .timeout(1s) halts the request and propagates a 500 TimeoutException
        webTestClient.get()
                .uri("/api/resilience/slow")
                .exchange()
                // Assert that the request timed out and was mapped to a 500 error by the exception handler
                .expectStatus().is5xxServerError() 
                .expectBody()
                // Assert that the custom message detail informs about the 1000ms duration timeout event
                .jsonPath("$.detail").value(detail -> 
                        assertThat(detail.toString()).contains("1000ms"));
    }

    @Test
    void scenario6_ExponentialRetry() {
        // GIVEN: An unstable service mocked to fail with 503 twice and succeed with 200 on the third attempt
        mockWebServer.enqueue(new MockResponse().setResponseCode(503));
        mockWebServer.enqueue(new MockResponse().setResponseCode(503));
        mockWebServer.enqueue(new MockResponse().setBody("SUCCESS"));

        // Setup our reactive WebClient targeting the mock server
        WebClient webClient = WebClient.builder().baseUrl(mockWebServer.url("/").toString()).build();

        // WHEN: Triggering the service call with retryWhen backoff logic
        // THEN: Verify via StepVerifier that the pipeline successfully retries, absorbs the 503s, and finishes with complete
        StepVerifier.create(resilienceService.callUnstableService(webClient))
                // Expect that the third attempt successfully emits "SUCCESS"
                .expectNext("SUCCESS")
                // Verify that the stream finishes cleanly
                .verifyComplete();
    }

    @Test
    void scenario7_FilterLevelError() {
        // GIVEN: A functional endpoint `/api/resilience/secure` with an invalid security key header
        // WHEN: Dispatching a request
        // THEN: Verify that the filter-level exception is intercepted and formatted correctly by the global handler
        webTestClient.get()
                .uri("/api/resilience/secure")
                .header("X-Security-Key", "wrong")
                .exchange()
                // Assert filter-level rejection maps to 500
                .expectStatus().is5xxServerError()
                .expectBody()
                // Validate that the exception message thrown by the functional filter is serialized in the response body
                .jsonPath("$.detail").isEqualTo("Missing security key");
    }
}
