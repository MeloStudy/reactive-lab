package com.reactivelab.store.service;

import com.reactivelab.store.client.InventoryClient;
import com.reactivelab.store.model.Order;
import com.reactivelab.store.model.Product;
import com.reactivelab.store.repository.OrderRepository;
import com.reactivelab.store.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.Context;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import reactor.blockhound.BlockHound;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    static {
        BlockHound.install();
    }

    @Mock
    private ProductRepository productRepository;
    @Mock
    private InventoryClient inventoryClient;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldPlaceOrderSuccessfully() {
        // Arrange
        String productId = "PROD-123";
        Product product = Product.builder().id(productId).name("Test Product").build();
        InventoryClient.InventoryResponse inventory = InventoryClient.InventoryResponse.builder()
                .productId(productId)
                .stock(10)
                .price(100.0)
                .status("AVAILABLE")
                .build();
        
        Order savedOrder = Order.builder()
                .id(1L)
                .productId(productId)
                .customerName("John Doe")
                .quantity(2)
                .totalPrice(200.0)
                .status("COMPLETED")
                .build();

        when(productRepository.findById(productId)).thenReturn(Mono.just(product));
        when(inventoryClient.getInventory(productId)).thenReturn(Mono.just(inventory));
        when(orderRepository.save(any(Order.class))).thenReturn(Mono.just(savedOrder));

        // Act & Assert
        StepVerifier.create(orderService.placeOrder(productId, "John Doe", 2)
                        .contextWrite(Context.of("X-Correlation-ID", "TEST-TRACE-123")))
                .expectNextMatches(order -> order.getId().equals(1L) && order.getTotalPrice() == 200.0)
                .verifyComplete();

        verify(productRepository).findById(productId);
        verify(inventoryClient).getInventory(productId);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldFailWhenInsufficientStock() {
        // Arrange
        String productId = "PROD-123";
        Product product = Product.builder().id(productId).build();
        InventoryClient.InventoryResponse inventory = InventoryClient.InventoryResponse.builder()
                .productId(productId)
                .stock(1)
                .price(100.0)
                .status("AVAILABLE")
                .build();

        when(productRepository.findById(productId)).thenReturn(Mono.just(product));
        when(inventoryClient.getInventory(productId)).thenReturn(Mono.just(inventory));

        // Act & Assert
        StepVerifier.create(orderService.placeOrder(productId, "John Doe", 5))
                .expectErrorMessage("Insufficient stock for product: PROD-123")
                .verify();

        verify(orderRepository, never()).save(any());
    }

    @Test
    void shouldGetAllOrders() {
        Order order1 = Order.builder().id(1L).build();
        Order order2 = Order.builder().id(2L).build();
        when(orderRepository.findAll()).thenReturn(Flux.just(order1, order2));

        StepVerifier.create(orderService.getAllOrders())
                .expectNext(order1, order2)
                .verifyComplete();
    }

    @Test
    void shouldGetOrderById() {
        Order order = Order.builder().id(1L).build();
        when(orderRepository.findById(1L)).thenReturn(Mono.just(order));

        StepVerifier.create(orderService.getOrderById(1L))
                .expectNext(order)
                .verifyComplete();
    }

    @Test
    void shouldUpdateOrderStatus() {
        Order order = Order.builder().id(1L).status("COMPLETED").build();
        when(orderRepository.findById(1L)).thenReturn(Mono.just(order));
        when(orderRepository.save(any(Order.class))).thenReturn(Mono.just(order));

        StepVerifier.create(orderService.updateOrderStatus(1L, "SHIPPED"))
                .expectNextMatches(o -> o.getStatus().equals("SHIPPED"))
                .verifyComplete();
    }

    @Test
    void shouldDeleteOrder() {
        when(orderRepository.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(orderService.deleteOrder(1L))
                .verifyComplete();
    }
}
