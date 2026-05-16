package com.reactivelab.mongodb.repository;

import com.reactivelab.mongodb.model.Sale;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface SaleRepository extends ReactiveMongoRepository<Sale, String> {
}
