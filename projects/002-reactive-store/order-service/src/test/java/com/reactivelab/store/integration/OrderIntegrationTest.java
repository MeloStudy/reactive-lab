package com.reactivelab.store.integration;

import com.reactivelab.store.model.Product;
import com.reactivelab.store.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class OrderIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldSaveAndRetrieveProductFromRealMongo() {
        // Arrange
        Product product = Product.builder()
                .id("PROD-TEST")
                .name("Integration Test Product")
                .build();

        // Act & Assert
        productRepository.save(product)
                .as(StepVerifier::create)
                .expectNextMatches(p -> p.getId().equals("PROD-TEST"))
                .verifyComplete();

        productRepository.findById("PROD-TEST")
                .as(StepVerifier::create)
                .assertNext(p -> {
                    assertThat(p.getName()).isEqualTo("Integration Test Product");
                })
                .verifyComplete();
    }
}
