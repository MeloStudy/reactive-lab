package com.reactivelab.resilience.service;

import com.reactivelab.resilience.model.Item;
import com.reactivelab.resilience.model.ProductNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
@Service
public class ResilienceService {

    /**
     * Scenario 1 fallback service call.
     * Throws an exception if the id is "fail".
     */
    public Mono<Item> fetchItem(String id) {
        if ("fail".equals(id)) {
            log.warn("Item fetch failed for id: fail. Emitting error signal.");
            return Mono.error(new RuntimeException("Simulated failure"));
        }
        return Mono.just(new Item(id, "Real Item", 99.99));
    }

    /**
     * Scenario 2 domain service call.
     * Throws ProductNotFoundException if the product is not found.
     */
    public Mono<Item> fetchProduct(String id) {
        if ("unknown".equals(id)) {
            log.warn("Product with id: {} not found. Emitting ProductNotFoundException.", id);
            return Mono.error(new ProductNotFoundException(id));
        }
        return Mono.just(new Item(id, "Premium Product", 149.99));
    }

    /**
     * Scenario 5 simulation of database sluggishness.
     * Delays emissions by 5 seconds to trigger server-side timeout.
     */
    public Mono<String> getSlowData() {
        log.info("Accessing sluggish database data source...");
        return Mono.just("Slow Data").delayElement(Duration.ofSeconds(5));
    }

    /**
     * Scenario 6 resilient client retry chain.
     * Applies exponential backoff retry logic to handle transient web server failures.
     */
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
