package com.reactivelab.r2dbc;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
@RestController
@RequestMapping("/api/persistence")
@RequiredArgsConstructor
public class PersistenceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersistenceApplication.class, args);
    }

    private final ProductRepository productRepository;
    private final ProductService productService;

    @GetMapping("/products")
    public Flux<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @PostMapping("/products")
    public Mono<Product> createProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }

    @PostMapping("/purchase")
    public Mono<Order> purchase(@RequestParam Long productId, @RequestParam Integer quantity) {
        return productService.purchaseProduct(productId, quantity);
    }
}
