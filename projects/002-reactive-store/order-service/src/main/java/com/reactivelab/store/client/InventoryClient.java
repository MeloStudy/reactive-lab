package com.reactivelab.store.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Component
public class InventoryClient {

    private final WebClient webClient;

    public InventoryClient(WebClient.Builder builder, @Value("${services.inventory.url}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public Mono<InventoryResponse> getInventory(String productId) {
        return Mono.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault("X-Correlation-ID", "UNKNOWN");
            log.info("Calling inventory-service for product: {} | Correlation-ID: {}", productId, correlationId);
            
            return webClient.get()
                    .uri("/inventory/{id}", productId)
                    .header("X-Correlation-ID", correlationId)
                    .retrieve()
                    .bodyToMono(InventoryResponse.class)
                    .timeout(Duration.ofMillis(800))
                    .doOnError(e -> log.error("Error calling inventory-service: {}", e.getMessage()))
                    .onErrorResume(e -> Mono.just(InventoryResponse.builder()
                            .productId(productId)
                            .status("UNKNOWN")
                            .price(0.0)
                            .stock(0)
                            .build()));
        });
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class InventoryResponse {
        private String productId;
        private Integer stock;
        private Double price;
        private String status;
    }
}
