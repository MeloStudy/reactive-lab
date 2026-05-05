package com.reactivelab.resilience;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
@RestController
@RequestMapping("/api/resilience")
@RequiredArgsConstructor
class ResilienceController {

    private final ResilienceService resilienceService;

    // Scenario 1: Local Fallback (onErrorReturn)
    @GetMapping("/items/{id}")
    public Mono<Item> getItem(@PathVariable String id) {
        return resilienceService.fetchItem(id)
                .onErrorReturn(Item.builder().id("0").name("Default Item").price(0.0).build());
    }

    // Scenario 2: Controller @ExceptionHandler
    @GetMapping("/products/{id}")
    public Mono<Item> getProduct(@PathVariable String id) {
        return resilienceService.fetchProduct(id);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<String> handleProductNotFound(ProductNotFoundException ex) {
        log.warn("Handled by @ExceptionHandler: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    // Scenario 5: Server-side Timeout
    @GetMapping("/slow")
    public Mono<String> getSlowData() {
        return resilienceService.getSlowData()
                .timeout(Duration.ofSeconds(1)); // Server-side timeout
    }
}

@Slf4j
@org.springframework.stereotype.Service
class ResilienceService {

    public Mono<Item> fetchItem(String id) {
        if ("fail".equals(id)) return Mono.error(new RuntimeException("Simulated failure"));
        return Mono.just(new Item(id, "Real Item", 99.99));
    }

    public Mono<Item> fetchProduct(String id) {
        if ("unknown".equals(id)) return Mono.error(new ProductNotFoundException(id));
        return Mono.just(new Item(id, "Premium Product", 149.99));
    }

    public Mono<String> getSlowData() {
        return Mono.just("Slow Data").delayElement(Duration.ofSeconds(5));
    }

    // Scenario 6: Exponential Retry (used with WebClient in tests)
    public Mono<String> callUnstableService(WebClient webClient) {
        return webClient.get()
                .uri("/unstable")
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.backoff(3, Duration.ofMillis(100))
                        .jitter(0.75)
                        .filter(throwable -> throwable instanceof RuntimeException))
                .log("RetryPipeline");
    }
}
