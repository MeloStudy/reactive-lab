package com.reactivelab.resilience;

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
import java.time.Duration;

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
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void scenario1_LocalFallback() {
        webTestClient.get()
                .uri("/api/resilience/items/fail")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("0")
                .jsonPath("$.name").isEqualTo("Default Item");
    }

    @Test
    void scenario2_ControllerExceptionHandler() {
        webTestClient.get()
                .uri("/api/resilience/products/unknown")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class).isEqualTo("Product with ID unknown not found");
    }

    @Test
    void scenario3_GlobalHandler_ProblemDetail() {
        // Trigger a generic RuntimeException via some unhandled path or logic
        // We'll call /api/resilience/products/error if we added it, or just use /api/resilience/secure without header
        webTestClient.get()
                .uri("/api/resilience/secure") // Without X-Security-Key header
                .exchange()
                .expectStatus().is5xxServerError()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("$.title").isEqualTo("Reactive Lab Error")
                .jsonPath("$.status").isEqualTo(500)
                .jsonPath("$.detail").isEqualTo("Missing security key");
    }

    @Test
    void scenario4_ContextPropagation_CorrelationId() {
        webTestClient.get()
                .uri("/api/resilience/secure")
                .header("X-Correlation-ID", "test-trace-123")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.correlation_id").isEqualTo("test-trace-123");
    }

    @Test
    void scenario5_ServerSideTimeout() {
        webTestClient.get()
                .uri("/api/resilience/slow")
                .exchange()
                .expectStatus().is5xxServerError() // Timeout maps to 500 in global handler
                .expectBody()
                .jsonPath("$.detail").value(detail -> 
                        assertThat(detail.toString()).contains("1000ms"));
    }

    @Test
    void scenario6_ExponentialRetry() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(503));
        mockWebServer.enqueue(new MockResponse().setResponseCode(503));
        mockWebServer.enqueue(new MockResponse().setBody("SUCCESS"));

        WebClient webClient = WebClient.builder().baseUrl(mockWebServer.url("/").toString()).build();

        StepVerifier.create(resilienceService.callUnstableService(webClient))
                .expectNext("SUCCESS")
                .verifyComplete();
    }

    @Test
    void scenario7_FilterLevelError() {
        webTestClient.get()
                .uri("/api/resilience/secure")
                .header("X-Security-Key", "wrong")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Missing security key");
    }
}
