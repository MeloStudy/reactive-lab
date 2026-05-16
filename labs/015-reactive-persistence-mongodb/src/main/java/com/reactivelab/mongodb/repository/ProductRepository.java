package com.reactivelab.mongodb.repository;

import com.reactivelab.mongodb.model.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
}
