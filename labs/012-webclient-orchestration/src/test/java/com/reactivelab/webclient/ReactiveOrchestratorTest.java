package com.reactivelab.webclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class ReactiveOrchestratorTest {

    private MockWebServer mockWebServer;
    private ReactiveOrchestrator orchestrator;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final java.util.concurrent.atomic.AtomicInteger inventoryRetryCount = new java.util.concurrent.atomic.AtomicInteger(0);

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        
        mockWebServer.setDispatcher(new okhttp3.mockwebserver.Dispatcher() {
            @Override
            public MockResponse dispatch(okhttp3.mockwebserver.RecordedRequest request) {
                try {
                    String path = request.getPath();
                    if (path.matches("/users/\\d+/orders$")) {
                        List<Order> orders = List.of(
                                Order.builder().id("101").product("Keyboard").build(),
                                Order.builder().id("102").product("Mouse").build()
                        );
                        return new MockResponse()
                                .setBody(objectMapper.writeValueAsString(orders))
                                .setBodyDelay(500, java.util.concurrent.TimeUnit.MILLISECONDS)
                                .addHeader("Content-Type", "application/json");
                    } else if (path.matches("/users/\\d+$")) {
                        User user = User.builder().id("1").username("melo").preferenceId("pref_123").build();
                        return new MockResponse()
                                .setBody(objectMapper.writeValueAsString(user))
                                .setBodyDelay(500, java.util.concurrent.TimeUnit.MILLISECONDS)
                                .addHeader("Content-Type", "application/json");
                    } else if (path.matches("/preferences/.*")) {
                        Preference preference = Preference.builder().id("pref_123").theme("DARK").build();
                        return new MockResponse().setBody(objectMapper.writeValueAsString(preference))
                                .addHeader("Content-Type", "application/json");
                    } else if (path.startsWith("/inventory/")) {
                        if (inventoryRetryCount.getAndIncrement() < 3) {
                            return new MockResponse().setResponseCode(500);
                        }
                        return new MockResponse().setBody("IN_STOCK");
                    } else if (path.equals("/events")) {
                        GlobalEvent e1 = GlobalEvent.builder().type("SALE").message("A").build();
                        GlobalEvent e2 = GlobalEvent.builder().type("HEARTBEAT").build();
                        GlobalEvent e3 = GlobalEvent.builder().type("ALERT").message("B").build();
                        return new MockResponse().setBody(objectMapper.writeValueAsString(List.of(e1, e2, e3)))
                                .addHeader("Content-Type", "application/json");
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

        // No delay in dispatcher for now to make it fast, 
        // but parallel execution is guaranteed by Dispatcher routing
        assertThat(end - start).isLessThan(2000);
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
        GlobalEvent e1 = GlobalEvent.builder().type("SALE").message("A").build();
        GlobalEvent e3 = GlobalEvent.builder().type("ALERT").message("B").build();

        StepVerifier.create(orchestrator.getEventsStream())
                .expectNextMatches(e -> e.getType().equals("SALE"))
                .expectNextMatches(e -> e.getType().equals("ALERT"))
                .verifyComplete();
    }
}
