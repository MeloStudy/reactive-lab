package com.reactivelab.r2dbc;

import io.r2dbc.postgresql.codec.Json;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("products")
class Product {
    @Id
    private Long id;
    private String name;
    private Double price;
    private Integer stock;
    private Json metadata; // PostgreSQL JSONB
}

interface ProductRepository extends ReactiveCrudRepository<Product, Long> {
    Flux<Product> findByNameContaining(String name);
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("orders")
class Order {
    @Id
    private Long id;
    private Long productId;
    private Integer quantity;
    private Double totalAmount;
}

interface OrderRepository extends ReactiveCrudRepository<Order, Long> {
}
