package com.reactivelab.store.config;

import com.reactivelab.store.model.Product;
import com.reactivelab.store.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        log.info("Refreshing MongoDB data for the lab...");
        productRepository.deleteAll()
                .thenMany(seedProducts())
                .subscribe(
                        product -> log.info("Seeded product: {}", product.getId()),
                        error -> log.error("Error seeding MongoDB: {}", error.getMessage()),
                        () -> log.info("Data initialization completed.")
                );
    }

    private Flux<Product> seedProducts() {
        List<Product> products = List.of(
                Product.builder().id("PROD-001").name("Reactive Spring in Action").category("Books").description("Master WebFlux with ease").build(),
                Product.builder().id("PROD-002").name("Python Legacy Adapter").category("Hardware").description("Old but gold").build(),
                Product.builder().id("PROD-003").name("Out of Stock Item").category("Generic").description("Should fail due to inventory").build(),
                Product.builder().id("PROD-004").name("Slow Response Gadget").category("Electronics").description("Tests your patience and timeouts").build()
        );
        return productRepository.saveAll(products);
    }
}
