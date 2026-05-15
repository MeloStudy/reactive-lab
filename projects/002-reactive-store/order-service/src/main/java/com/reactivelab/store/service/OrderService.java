package com.reactivelab.store.service;

import com.reactivelab.store.client.InventoryClient;
import com.reactivelab.store.model.Order;
import com.reactivelab.store.repository.OrderRepository;
import com.reactivelab.store.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductRepository productRepository;
    private final InventoryClient inventoryClient;
    private final OrderRepository orderRepository;

    public Mono<Order> placeOrder(String productId, String customerName, Integer quantity) {
        return Mono.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault("X-Correlation-ID", "UNKNOWN");
            log.info("[{}] Processing order for product: {} x {}", correlationId, productId, quantity);

            return productRepository.findById(productId)
                    .switchIfEmpty(Mono.error(new RuntimeException("Product not found in catalog: " + productId)))
                    .flatMap(product -> inventoryClient.getInventory(productId)
                            .flatMap(inventory -> {
                                if (inventory.getStock() < quantity) {
                                    return Mono.error(new RuntimeException("Insufficient stock for product: " + productId));
                                }
                                
                                Order order = Order.builder()
                                        .customerName(customerName)
                                        .productId(productId)
                                        .quantity(quantity)
                                        .totalPrice(inventory.getPrice() * quantity)
                                        .status("COMPLETED")
                                        .createdAt(LocalDateTime.now())
                                        .build();
                                
                                log.info("[{}] Saving order to SQLite for customer: {}", correlationId, customerName);
                                return orderRepository.save(order);
                            })
                    )
                    .doOnSuccess(order -> log.info("[{}] Order placed successfully: {}", correlationId, order.getId()))
                    .doOnError(e -> log.error("[{}] Order failed: {}", correlationId, e.getMessage()));
        });
    }

    public Flux<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Mono<Order> getOrderById(Long id) {
        return orderRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Order not found: " + id)));
    }

    public Mono<Order> updateOrderStatus(Long id, String status) {
        return orderRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Order not found: " + id)))
                .flatMap(order -> {
                    order.setStatus(status);
                    return orderRepository.save(order);
                });
    }

    public Mono<Void> deleteOrder(Long id) {
        return orderRepository.deleteById(id);
    }
}
