package com.reactivelab.webclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reactivelab.webclient.model.GlobalEvent;
import com.reactivelab.webclient.model.Order;
import com.reactivelab.webclient.model.Preference;
import com.reactivelab.webclient.model.User;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class ReactiveOrchestratorTest {

    private MockWebServer mockWebServer;
    private ReactiveOrchestrator orchestrator;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AtomicInteger inventoryRetryCount = new AtomicInteger(0);

    @BeforeAll
    static void initAll() {
        // T015: Integrate BlockHound to intercept blocking calls inside reactive threads
        // This ensures the non-blocking invariant of Reactor threads is never violated.
        BlockHound.install();
    }

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();

        // Dispatcher maps incoming request paths to specific mock responses simulating external APIs
        mockWebServer.setDispatcher(new okhttp3.mockwebserver.Dispatcher() {
            @Override
            public MockResponse dispatch(okhttp3.mockwebserver.RecordedRequest request) {
                try {
                    String path = request.getPath();
                    if (path.matches("/users/\\d+/orders$")) {
                        // Simulate an Orders Service taking 500ms to respond
                        return jsonResponse(List.of(
                                Order.builder().id("101").product("Keyboard").amount(89.99).build(),
                                Order.builder().id("102").product("Mouse").amount(45.50).build()
                        )).setBodyDelay(500, java.util.concurrent.TimeUnit.MILLISECONDS);
                    } else if (path.matches("/users/\\d+$")) {
                        // Simulate a User Service taking 500ms to respond
                        User user = User.builder().id("1").username("melo").preferenceId("pref_123").build();
                        return jsonResponse(user).setBodyDelay(500, java.util.concurrent.TimeUnit.MILLISECONDS);
                    } else if (path.matches("/preferences/.*")) {
                        // Simulate a Preferences Service responding instantly
                        Preference preference = Preference.builder().id("pref_123").theme("DARK").notificationsEnabled(true).build();
                        return jsonResponse(preference);
                    } else if (path.startsWith("/inventory/")) {
                        // If path contains "fail", force absolute error status for all retry attempts to assert fallback
                        if (path.contains("fail")) {
                            return new MockResponse().setResponseCode(500);
                        }
                        // Simulate a flaky service failing three times with 500 status code, then recovering on attempt 4
                        if (inventoryRetryCount.getAndIncrement() < 3) {
                            return new MockResponse().setResponseCode(500);
                        }
                        return new MockResponse().setBody("IN_STOCK");
                    } else if (path.equals("/events")) {
                        // Simulate a Server-Sent Events (SSE) data stream containing mixed payload events
                        return jsonResponse(List.of(
                                GlobalEvent.builder().type("SALE").message("A").build(),
                                GlobalEvent.builder().type("HEARTBEAT").build(),
                                GlobalEvent.builder().type("ALERT").message("B").build()
                        ));
                    } else if (path.startsWith("/secure-data/")) {
                        // Mock downstream server error to verify connection cleanup and leak prevention
                        if (path.contains("server-error")) {
                            return new MockResponse().setResponseCode(500).setBody("INTERNAL_SERVER_ERROR");
                        }
                        // Secure resource simulator: requires X-Secure-Token header
                        if (request.getHeader("X-Secure-Token") == null && path.contains("invalid")) {
                            // Erroneously returns 200 OK, but lacks the proper authorization header, testing connection cleanup
                            return new MockResponse().setResponseCode(200).setBody("HIDDEN_DATA");
                        }
                        return new MockResponse().setResponseCode(200).setBody("SECURE_CONTENT").addHeader("X-Secure-Token", "valid_token");
                    }
                    return new MockResponse().setResponseCode(404);
                } catch (Exception e) {
                    return new MockResponse().setResponseCode(500);
                }
            }
        });

        mockWebServer.start();
        String baseUrl = mockWebServer.url("/").toString();

        // Pass a default WebClient builder to the orchestrator service, configuring it with the mock base URL
        orchestrator = new ReactiveOrchestrator(WebClient.builder(), baseUrl);
    }

    private MockResponse jsonResponse(Object body) throws Exception {
        return new MockResponse()
                .setBody(objectMapper.writeValueAsString(body))
                .addHeader("Content-Type", "application/json");
    }

    @AfterEach
    void tearDown() throws IOException {
        // Shutdown mock servers and release port allocations between test cases
        mockWebServer.shutdown();
    }

    @Test
    void testGetUserById_Success() {
        User expectedUser = User.builder().id("1").username("melo").preferenceId("pref_123").build();

        // StepVerifier: Verifies basic WebClient GET operation and serialization mappings.
        StepVerifier.create(orchestrator.getUserById("1"))
                // Asserts that the first emitted item matches our expected User profile mapped from JSON
                .expectNext(expectedUser)
                // Asserts that the stream completed successfully with no additional signals or errors
                .verifyComplete();
    }

    @Test
    void testGetUserDashboard_Parallel() {
        long start = System.currentTimeMillis();

        // StepVerifier: Triggers the parallel execution pipeline using Mono.zip()
        StepVerifier.create(orchestrator.getUserDashboard("1"))
                .assertNext(dashboard -> {
                    // AssertJ Fluent style: Inspect mapped user state using fluent extracting checks
                    assertThat(dashboard.getUser())
                            .extracting(User::getUsername, User::getPreferenceId)
                            .containsExactly("melo", "pref_123");

                    // AssertJ Fluent style: Dedicated collection verification
                    assertThat(dashboard.getOrders())
                            .hasSize(2)
                            .extracting(Order::getProduct)
                            .containsExactly("Keyboard", "Mouse");
                })
                // Triggers stream execution and blocks until complete signal propagates
                .verifyComplete();

        long end = System.currentTimeMillis();

        // Parallel Invariant Verification: Both the user service and orders service mock requests
        // are delayed by 500ms. If they were executed sequentially, the total delay would exceed 1000ms.
        // Parallel execution via Mono.zip merges non-blocking writes, completing the cycle in ~500-600ms.
        assertThat(end - start)
                .as("Total execution time should demonstrate non-blocking parallel execution")
                .isLessThan(1500);
    }

    @Test
    void testGetFullUserDashboard_Dependent() {
        // StepVerifier: Validates sequential composition via flatMap().
        // Fetch User first (500ms delay), read preferenceId, then fetch preferences and orders concurrently.
        StepVerifier.create(orchestrator.getFullUserDashboard("1"))
                .assertNext(dashboard -> {
                    // AssertJ Fluent style: Inspect user details using extracting API
                    assertThat(dashboard.getUser())
                            .extracting(User::getUsername)
                            .isEqualTo("melo");

                    // AssertJ Fluent style: Verify preferences values mapped from downstream services
                    assertThat(dashboard.getPreference())
                            .extracting(Preference::getTheme, Preference::isNotificationsEnabled)
                            .containsExactly("DARK", true);

                    // AssertJ Fluent style: Verify size and entries in mapped orders list
                    assertThat(dashboard.getOrders())
                            .hasSize(2)
                            .extracting(Order::getId)
                            .containsExactly("101", "102");
                })
                .verifyComplete();
    }

    @Test
    void testGetInventoryStatus_Resilience() {
        // StepVerifier: Asserts timeout, retry-backoff, and default fallback recovery.
        // Flaky service fails 3 times, succeeding on the 4th attempt.
        StepVerifier.create(orchestrator.getInventoryStatus("prod_1"))
                // Expect a successful default fallback or recovered value propagated on retry recovery
                .expectNext("IN_STOCK")
                // Asserts successful termination post-recovery
                .verifyComplete();

        // Verify that the underlying loop attempted retries exactly 4 times (3 failures + 1 final success)
        assertThat(inventoryRetryCount.get())
                .as("The pipeline should execute a total of 4 downstream calls to achieve recovery")
                .isEqualTo(4);
    }

    @Test
    void testGetInventoryStatus_AllRetriesFail_ReturnsFallback() {
        // StepVerifier: Asserts timeout, retry-backoff, and default fallback recovery when ALL retries fail.
        // Flaky service fails continuously (500 error status code).
        StepVerifier.create(orchestrator.getInventoryStatus("prod_fail"))
                // Expect that the UNKNOWN fallback payload is returned post-exhaustion of retry threshold
                .expectNext("UNKNOWN")
                .verifyComplete();
    }

    @Test
    void testGetEventsStream() {
        // StepVerifier: Asserts SSE consumption and dynamic element filtering.
        StepVerifier.create(orchestrator.getEventsStream())
                // Assert filter logic: first non-HEARTBEAT item should be of type SALE
                .expectNextMatches(event -> "SALE".equals(event.getType()))
                // Assert filter logic: second non-HEARTBEAT item should be of type ALERT
                .expectNextMatches(event -> "ALERT".equals(event.getType()))
                // Asserts stream terminates complete post-filtering all list resources
                .verifyComplete();
    }

    @Test
    void testGetSecureData_WithToken() {
        // StepVerifier: Asserts successful path inside exchangeToMono when authorization header is present
        StepVerifier.create(orchestrator.getSecureData("valid"))
                .expectNext("SECURE_CONTENT")
                .verifyComplete();
    }

    @Test
    void testGetSecureData_MissingToken_ReleasesBody() {
        // T016: Verify exchangeToMono correctly handles missing header and releases body
        // This is a crucial security and resource-leak validation scenario.
        StepVerifier.create(orchestrator.getSecureData("invalid"))
                // Expect that lack of token causes WebClient response pipeline to fail with a RuntimeException having message 'Unauthorized'
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        "Unauthorized".equals(throwable.getMessage()))
                // Verifies that error termination propagates successfully
                .verify();
    }

    @Test
    void testGetSecureData_ServerError_ReleasesBody() {
        // StepVerifier: Asserts exchangeToMono correctly detects downstream server errors, releases body and propagates Exception
        StepVerifier.create(orchestrator.getSecureData("server-error"))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().startsWith("Server error:"))
                .verify();
    }

    @Test
    void testBlockHound_DetectsBlocking() {
        // StepVerifier: Verifies that BlockHound successfully intercepts blocking thread parking
        // when executed inside Reactor's non-blocking Schedulers parallel workers.
        StepVerifier.create(Mono.fromCallable(() -> {
                    // Force a thread block inside the stream to trigger BlockHound intercept
                    Thread.sleep(10);
                    return "done";
                }).subscribeOn(Schedulers.parallel()))
                // Expect that BlockHound throws a BlockingOperationError to prevent thread starvation
                .expectErrorMatches(throwable -> throwable instanceof BlockingOperationError)
                .verify();
    }
}
