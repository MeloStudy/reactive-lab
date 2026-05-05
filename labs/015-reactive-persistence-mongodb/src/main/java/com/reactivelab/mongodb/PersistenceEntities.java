package com.reactivelab.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "system_logs")
class LogEntry {
    @Id
    private String id;
    private String message;
    private String level;
    private Instant timestamp;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products")
class Product {
    @Id
    private String id;
    private String name;
    private Double price;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "sales")
class Sale {
    @Id
    private String id;
    private String category;
    private Double amount;
    private Integer quantity;
}

interface LogRepository extends ReactiveMongoRepository<LogEntry, String> {
    @Tailable
    Flux<LogEntry> findByLevel(String level);
    
    @Tailable
    Flux<LogEntry> findAllBy();
}

interface ProductRepository extends ReactiveMongoRepository<Product, String> {
}

interface SaleRepository extends ReactiveMongoRepository<Sale, String> {
}
