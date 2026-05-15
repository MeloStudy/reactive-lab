package com.reactivelab.store.controller;

import com.reactivelab.store.model.Order;
import com.reactivelab.store.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@WebFluxTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private OrderService orderService;

    @Test
    void shouldReturnCreatedOrder() {
        // Arrange
        Order order = Order.builder()
                .id(1L)
                .customerName("Alice")
                .productId("PROD-1")
                .quantity(1)
                .status("COMPLETED")
                .build();

        when(orderService.placeOrder(anyString(), anyString(), anyInt())).thenReturn(Mono.just(order));

        // Act & Assert
        webTestClient.post()
                .uri("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new OrderController.OrderRequest("PROD-1", "Alice", 1))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.customerName").isEqualTo("Alice")
                .jsonPath("$.status").isEqualTo("COMPLETED");
    }

    @Test
    void shouldReturnAllOrders() {
        Order order = Order.builder().id(1L).build();
        when(orderService.getAllOrders()).thenReturn(Flux.just(order));

        webTestClient.get()
                .uri("/orders")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Order.class)
                .hasSize(1);
    }

    @Test
    void shouldReturnOrderById() {
        Order order = Order.builder().id(1L).build();
        when(orderService.getOrderById(1L)).thenReturn(Mono.just(order));

        webTestClient.get()
                .uri("/orders/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1);
    }

    @Test
    void shouldUpdateStatus() {
        Order order = Order.builder().id(1L).status("SHIPPED").build();
        when(orderService.updateOrderStatus(eq(1L), anyString())).thenReturn(Mono.just(order));

        webTestClient.patch()
                .uri(uriBuilder -> uriBuilder.path("/orders/1")
                        .queryParam("status", "SHIPPED")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("SHIPPED");
    }

    @Test
    void shouldDeleteOrder() {
        when(orderService.deleteOrder(1L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/orders/1")
                .exchange()
                .expectStatus().isNoContent();
    }
}
