package com.reactivelab.r2dbc.service;

import com.reactivelab.r2dbc.model.Order;
import com.reactivelab.r2dbc.model.Product;
import com.reactivelab.r2dbc.repository.OrderRepository;
import com.reactivelab.r2dbc.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final DatabaseClient databaseClient;
    private final R2dbcEntityTemplate entityTemplate;

    @Transactional
    public Mono<Order> purchaseProduct(Long productId, Integer quantity) {
        log.info("Starting purchase for product: {} quantity: {}", productId, quantity);
        
        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(new RuntimeException("Product not found")))
                .flatMap(product -> {
                    if (product.getStock() < quantity) {
                        return Mono.error(new RuntimeException("Insufficient stock"));
                    }
                    
                    product.setStock(product.getStock() - quantity);
                    
                    return productRepository.save(product)
                            .then(orderRepository.save(Order.builder()
                                    .productId(productId)
                                    .quantity(quantity)
                                    .totalAmount(product.getPrice() * quantity)
                                    .build()));
                })
                .doOnSuccess(order -> log.info("Purchase completed: {}", order.getId()))
                .doOnError(err -> log.error("Purchase failed, rolling back: {}", err.getMessage()));
    }

    /**
     * Using DatabaseClient for raw SQL flexibility.
     */
    public Flux<Product> searchByName(String name) {
        log.info("Searching products by name using DatabaseClient: {}", name);
        return databaseClient.sql("SELECT * FROM products WHERE name ILIKE :name")
                .bind("name", "%" + name + "%")
                .map((row, metadata) -> Product.builder()
                        .id(row.get("id", Long.class))
                        .name(row.get("name", String.class))
                        .price(row.get("price", Double.class))
                        .stock(row.get("stock", Integer.class))
                        .build())
                .all();
    }

    /**
     * Using R2dbcEntityTemplate for programmatic criteria search.
     */
    public Flux<Product> searchByPriceRange(Double min, Double max) {
        log.info("Searching products by price range using R2dbcEntityTemplate: {} - {}", min, max);
        return entityTemplate.select(Product.class)
                .matching(Query.query(Criteria.where("price").between(min, max)))
                .all();
    }
}
