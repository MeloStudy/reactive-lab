package com.reactivelab.webclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
     */
    public Mono<UserDashboard> getUserDashboard(String userId) {
        Mono<User> userMono = getUserById(userId);
        
        Mono<List<Order>> ordersMono = webClient.get()
                .uri("/users/{id}/orders", userId)
                .retrieve()
                .bodyToFlux(Order.class)
                .collectList();

        return Mono.zip(userMono, ordersMono)
                .map(tuple -> UserDashboard.builder()
                        .user(tuple.getT1())
                        .orders(tuple.getT2())
                        .build());
    }

    /**
     * Scenario 3: Dependent Calls
     */
    public Mono<UserDashboard> getFullUserDashboard(String userId) {
        return getUserById(userId)
                .flatMap(user -> {
                    Mono<List<Order>> ordersMono = webClient.get()
                            .uri("/users/{id}/orders", userId)
                            .retrieve()
                            .bodyToFlux(Order.class)
                            .collectList();

                    Mono<Preference> preferenceMono = webClient.get()
                            .uri("/preferences/{id}", user.getPreferenceId())
                            .retrieve()
                            .bodyToMono(Preference.class);

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
     */
    public Flux<GlobalEvent> getEventsStream() {
        return webClient.get()
                .uri("/events")
                .retrieve()
                .bodyToFlux(GlobalEvent.class)
                .filter(event -> !"HEARTBEAT".equals(event.getType()))
                .log("EventStream");
    }
}
