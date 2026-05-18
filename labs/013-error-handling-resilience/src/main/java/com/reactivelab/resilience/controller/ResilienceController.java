package com.reactivelab.resilience.controller;

import com.reactivelab.resilience.model.Item;
import com.reactivelab.resilience.model.ProductNotFoundException;
import com.reactivelab.resilience.service.ResilienceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@RestController
@RequestMapping("/api/resilience")
@RequiredArgsConstructor
public class ResilienceController {

    private final ResilienceService resilienceService;

    /**
     * Scenario 1: Local Fallback.
     * Demonstrates using `onErrorReturn` at the controller pipeline level to return a safe static default item.
     */
    @GetMapping("/items/{id}")
    public Mono<Item> getItem(@PathVariable String id) {
        log.info("Request received for item id: {}", id);
        return resilienceService.fetchItem(id)
                .onErrorReturn(Item.builder()
                        .id("0")
                        .name("Default Item")
                        .price(0.0)
                        .build());
    }

    /**
     * Scenario 2: Controller-level Exception Handling.
     * Emits a ProductNotFoundException, which is intercepted by the local @ExceptionHandler.
     */
    @GetMapping("/products/{id}")
    public Mono<Item> getProduct(@PathVariable String id) {
        log.info("Request received for product id: {}", id);
        return resilienceService.fetchProduct(id);
    }

    /**
     * Local handler for ProductNotFoundException.
     * Overrides default error attributes for client safety, returning 404.
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<String> handleProductNotFound(ProductNotFoundException ex) {
        log.warn("Local @ExceptionHandler intercepted ProductNotFoundException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Scenario 5: Server-side Timeout.
     * Implements a reactive timeout. If downstream database slow-sim fails to emit in 1 second,
     * triggers TimeoutException signal propagation.
     */
    @GetMapping("/slow")
    public Mono<String> getSlowData() {
        log.info("Request received for slow sluggish database response.");
        return resilienceService.getSlowData()
                .timeout(Duration.ofSeconds(1));
    }
}
