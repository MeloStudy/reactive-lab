package com.reactivelab.webclient.service;

import com.reactivelab.webclient.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
public class ReactiveOrchestrator {

    private final WebClient webClient;

    public ReactiveOrchestrator(WebClient.Builder webClientBuilder, String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    /**
     * Scenario 1: Simple Fetch
     */
    public Mono<User> getUserById(String id) {
        return webClient.get()
                .uri("/users/{id}", id)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response -> {
                    log.error("Client error for user {}: {}", id, response.statusCode());
                    return Mono.error(new RuntimeException("User not found"));
                })
                .bodyToMono(User.class);
    }

    /**
     * Scenario 2: Parallel Orchestration
     * Orchestrates concurrent non-blocking calls to User and Orders services.
     */
    public Mono<UserDashboard> getUserDashboard(String userId) {
        Mono<User> userMono = getUserById(userId);
        Mono<List<Order>> ordersMono = getOrdersByUserId(userId);

        return Mono.zip(userMono, ordersMono)
                .map(tuple -> UserDashboard.builder()
                        .user(tuple.getT1())
                        .orders(tuple.getT2())
                        .build());
    }

    /**
     * Scenario 3: Dependent Calls
     * Sequentially retrieves User, then triggers parallel calls for Orders and Preferences.
     */
    public Mono<UserDashboard> getFullUserDashboard(String userId) {
        return getUserById(userId)
                .flatMap(user -> {
                    Mono<List<Order>> ordersMono = getOrdersByUserId(userId);
                    Mono<Preference> preferenceMono = getPreferenceById(user.getPreferenceId());

                    return Mono.zip(ordersMono, preferenceMono)
                            .map(tuple -> UserDashboard.builder()
                                    .user(user)
                                    .orders(tuple.getT1())
                                    .preference(tuple.getT2())
                                    .build());
                });
    }

    /**
     * Scenario 4: Failover & Resilience
     * Product Inventory fetch with strict 2-second timeout and 3 backoff retries.
     */
    public Mono<String> getInventoryStatus(String productId) {
        return webClient.get()
                .uri("/inventory/{id}", productId)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(2))
                .retryWhen(Retry.backoff(3, Duration.ofMillis(100))
                        .filter(throwable -> throwable instanceof RuntimeException)) // Simplified for the lab
                .onErrorReturn("UNKNOWN");
    }

    /**
     * Scenario 5: Consuming the Stream
     * Connects to a Server-Sent Events stream, filtering out internal HEARTBEAT logs.
     */
    public Flux<GlobalEvent> getEventsStream() {
        return webClient.get()
                .uri("/events")
                .retrieve()
                .bodyToFlux(GlobalEvent.class)
                .filter(event -> !"HEARTBEAT".equals(event.getType()))
                .log("EventStream");
    }

    /**
     * Scenario 6: Advanced Body Control (Manual Consumption)
     * Programmatically checks status and headers using exchangeToMono.
     * Prevents connection pool starvation by explicitly releasing error or unauthorized request bodies.
     */
    public Mono<String> getSecureData(String id) {
        return webClient.get()
                .uri("/secure-data/{id}", id)
                .exchangeToMono(response -> {
                    // Check for HTTP status errors first
                    if (response.statusCode().isError()) {
                        log.error("Server error returned for secure data ID: {}, status: {}", id, response.statusCode());
                        return response.releaseBody()
                                .then(Mono.error(new RuntimeException("Server error: " + response.statusCode())));
                    }
                    // Validate authorization token header
                    if (response.headers().header("X-Secure-Token").isEmpty()) {
                        log.warn("Missing secure token header for ID: {}, releasing body", id);
                        return response.releaseBody()
                                .then(Mono.error(new RuntimeException("Unauthorized")));
                    }
                    return response.bodyToMono(String.class);
                });
    }

    /**
     * Reusable package-private endpoint helpers to decouple HTTP construction from Orchestration logic.
     */
    Mono<List<Order>> getOrdersByUserId(String userId) {
        return webClient.get()
                .uri("/users/{id}/orders", userId)
                .retrieve()
                .bodyToFlux(Order.class)
                .collectList();
    }

    Mono<Preference> getPreferenceById(String preferenceId) {
        return webClient.get()
                .uri("/preferences/{id}", preferenceId)
                .retrieve()
                .bodyToMono(Preference.class);
    }
}
