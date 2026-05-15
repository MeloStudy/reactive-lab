package com.reactivelab.r2dbc.controller;

import com.reactivelab.r2dbc.model.Order;
import com.reactivelab.r2dbc.model.Product;
import com.reactivelab.r2dbc.repository.ProductRepository;
import com.reactivelab.r2dbc.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/persistence")
@RequiredArgsConstructor
public class ProductController {

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

    @GetMapping("/products/search")
    public Flux<Product> search(@RequestParam String name) {
        return productService.searchByName(name);
    }

    @GetMapping("/products/range")
    public Flux<Product> range(@RequestParam Double min, @RequestParam Double max) {
        return productService.searchByPriceRange(min, max);
    }

    @PostMapping("/purchase")
    public Mono<Order> purchase(@RequestParam Long productId, @RequestParam Integer quantity) {
        return productService.purchaseProduct(productId, quantity);
    }
}
