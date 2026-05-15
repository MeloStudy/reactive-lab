package com.reactivelab.r2dbc.repository;

import com.reactivelab.r2dbc.model.Order;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {
}
