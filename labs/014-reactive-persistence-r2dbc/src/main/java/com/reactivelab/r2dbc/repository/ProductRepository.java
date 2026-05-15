package com.reactivelab.r2dbc.repository;

import com.reactivelab.r2dbc.model.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {
    Flux<Product> findByNameContaining(String name);
}
