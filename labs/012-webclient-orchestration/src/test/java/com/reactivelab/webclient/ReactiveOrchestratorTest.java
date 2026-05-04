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

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String baseUrl = mockWebServer.url("/").toString();
        orchestrator = new ReactiveOrchestrator(WebClient.builder(), baseUrl);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testGetUserById_Success() throws JsonProcessingException {
        User user = User.builder().id("1").username("melo").email("melo@lab.com").build();
        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(user))
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(orchestrator.getUserById("1"))
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void testGetUserDashboard_Parallel() throws JsonProcessingException {
        User user = User.builder().id("1").username("melo").build();
        List<Order> orders = List.of(
                Order.builder().id("101").product("Keyboard").build(),
                Order.builder().id("102").product("Mouse").build()
        );

        // Enqueue responses for User and Orders
        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(user))
                .setBodyDelay(500, TimeUnit.MILLISECONDS) // Artificial delay
                .addHeader("Content-Type", "application/json"));
        
        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(orders))
                .setBodyDelay(500, TimeUnit.MILLISECONDS) // Artificial delay
                .addHeader("Content-Type", "application/json"));

        long start = System.currentTimeMillis();
        StepVerifier.create(orchestrator.getUserDashboard("1"))
                .assertNext(dashboard -> {
                    assertThat(dashboard.getUser()).isEqualTo(user);
                    assertThat(dashboard.getOrders()).hasSize(2);
                })
                .verifyComplete();
        long end = System.currentTimeMillis();

        // If they are parallel, total time should be ~500ms, not 1000ms
        assertThat(end - start).isLessThan(900);
    }

    @Test
    void testGetFullUserDashboard_Dependent() throws JsonProcessingException {
        User user = User.builder().id("1").preferenceId("pref_123").build();
        Preference preference = Preference.builder().id("pref_123").theme("DARK").build();
        List<Order> orders = List.of(Order.builder().id("101").build());

        // 1. User call
        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(user))
                .addHeader("Content-Type", "application/json"));
        
        // 2. Orders call
        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(orders))
                .addHeader("Content-Type", "application/json"));

        // 3. Preference call (depends on user.preferenceId)
        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(preference))
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(orchestrator.getFullUserDashboard("1"))
                .assertNext(dashboard -> {
                    assertThat(dashboard.getUser()).isEqualTo(user);
                    assertThat(dashboard.getPreference()).isEqualTo(preference);
                    assertThat(dashboard.getOrders()).hasSize(1);
                })
                .verifyComplete();
    }

    @Test
    void testGetInventoryStatus_Resilience() {
        // Enqueue 3 failures then 1 success
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        mockWebServer.enqueue(new MockResponse().setBody("IN_STOCK"));

        StepVerifier.create(orchestrator.getInventoryStatus("prod_1"))
                .expectNext("IN_STOCK")
                .verifyComplete();
    }

    @Test
    void testGetEventsStream() throws JsonProcessingException {
        GlobalEvent e1 = GlobalEvent.builder().type("SALE").message("A").build();
        GlobalEvent e2 = GlobalEvent.builder().type("HEARTBEAT").build();
        GlobalEvent e3 = GlobalEvent.builder().type("ALERT").message("B").build();

        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(List.of(e1, e2, e3)))
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(orchestrator.getEventsStream())
                .expectNext(e1)
                .expectNext(e3) // e2 is filtered out
                .verifyComplete();
    }
}
