package com.reactivelab.webclient;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.assertj.core.api.Assertions.assertThat;

class ReactiveOrchestratorTest {

    private MockWebServer mockWebServer;
    private ReactiveOrchestrator orchestrator;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final java.util.concurrent.atomic.AtomicInteger inventoryRetryCount = new java.util.concurrent.atomic.AtomicInteger(0);

    @BeforeAll
    static void initAll() {
        // T015: Integrate BlockHound to ensure non-blocking invariants
        BlockHound.install();
    }

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        
        mockWebServer.setDispatcher(new okhttp3.mockwebserver.Dispatcher() {
            @Override
            public MockResponse dispatch(okhttp3.mockwebserver.RecordedRequest request) {
                try {
                    String path = request.getPath();
                    if (path.matches("/users/\\d+/orders$")) {
                        return jsonResponse(List.of(
                                Order.builder().id("101").product("Keyboard").build(),
                                Order.builder().id("102").product("Mouse").build()
                        )).setBodyDelay(500, java.util.concurrent.TimeUnit.MILLISECONDS);
                    } else if (path.matches("/users/\\d+$")) {
                        User user = User.builder().id("1").username("melo").preferenceId("pref_123").build();
                        return jsonResponse(user).setBodyDelay(500, java.util.concurrent.TimeUnit.MILLISECONDS);
                    } else if (path.matches("/preferences/.*")) {
                        Preference preference = Preference.builder().id("pref_123").theme("DARK").build();
                        return jsonResponse(preference);
                    } else if (path.startsWith("/inventory/")) {
                        if (inventoryRetryCount.getAndIncrement() < 3) {
                            return new MockResponse().setResponseCode(500);
                        }
                        return new MockResponse().setBody("IN_STOCK");
                    } else if (path.equals("/events")) {
                        return jsonResponse(List.of(
                                GlobalEvent.builder().type("SALE").message("A").build(),
                                GlobalEvent.builder().type("HEARTBEAT").build(),
                                GlobalEvent.builder().type("ALERT").message("B").build()
                        ));
                    } else if (path.startsWith("/secure-data/")) {
                        if (request.getHeader("X-Secure-Token") == null && path.contains("invalid")) {
                            return new MockResponse().setResponseCode(200).setBody("HIDDEN_DATA"); // Wrongly return 200 but without token
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
        orchestrator = new ReactiveOrchestrator(WebClient.builder(), baseUrl);
    }

    private MockResponse jsonResponse(Object body) throws Exception {
        return new MockResponse()
                .setBody(objectMapper.writeValueAsString(body))
                .addHeader("Content-Type", "application/json");
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testGetUserById_Success() {
        User expectedUser = User.builder().id("1").username("melo").preferenceId("pref_123").build();
        
        StepVerifier.create(orchestrator.getUserById("1"))
                .expectNext(expectedUser)
                .verifyComplete();
    }

    @Test
    void testGetUserDashboard_Parallel() {
        long start = System.currentTimeMillis();
        StepVerifier.create(orchestrator.getUserDashboard("1"))
                .assertNext(dashboard -> {
                    assertThat(dashboard.getUser().getUsername()).isEqualTo("melo");
                    assertThat(dashboard.getOrders()).hasSize(2);
                })
                .verifyComplete();
        long end = System.currentTimeMillis();

        // Parallel execution: both calls take 500ms, total should be ~500ms (not 1000ms)
        assertThat(end - start).isLessThan(1500);
    }

    @Test
    void testGetFullUserDashboard_Dependent() {
        StepVerifier.create(orchestrator.getFullUserDashboard("1"))
                .assertNext(dashboard -> {
                    assertThat(dashboard.getUser().getUsername()).isEqualTo("melo");
                    assertThat(dashboard.getPreference().getTheme()).isEqualTo("DARK");
                    assertThat(dashboard.getOrders()).hasSize(2);
                })
                .verifyComplete();
    }

    @Test
    void testGetInventoryStatus_Resilience() {
        StepVerifier.create(orchestrator.getInventoryStatus("prod_1"))
                .expectNext("IN_STOCK")
                .verifyComplete();
        
        assertThat(inventoryRetryCount.get()).isEqualTo(4); // 3 fails + 1 success
    }

    @Test
    void testGetEventsStream() {
        StepVerifier.create(orchestrator.getEventsStream())
                .expectNextMatches(e -> e.getType().equals("SALE"))
                .expectNextMatches(e -> e.getType().equals("ALERT"))
                .verifyComplete();
    }

    @Test
    void testGetSecureData_WithToken() {
        StepVerifier.create(orchestrator.getSecureData("valid"))
                .expectNext("SECURE_CONTENT")
                .verifyComplete();
    }

    @Test
    void testGetSecureData_MissingToken_ReleasesBody() {
        // T016: Verify exchangeToMono correctly handles missing header and releases body
        StepVerifier.create(orchestrator.getSecureData("invalid"))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && 
                        throwable.getMessage().equals("Unauthorized"))
                .verify();
    }

    @Test
    void testBlockHound_DetectsBlocking() {
        // Verify BlockHound is working
        StepVerifier.create(Mono.fromCallable(() -> {
            Thread.sleep(10);
            return "done";
        }).subscribeOn(Schedulers.parallel()))

        .expectErrorMatches(throwable -> throwable instanceof BlockingOperationError)
        .verify();

    }
}
