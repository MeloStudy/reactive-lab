package com.reactivelab.r2dbc;

import com.reactivelab.r2dbc.model.Product;
import com.reactivelab.r2dbc.repository.OrderRepository;
import com.reactivelab.r2dbc.repository.ProductRepository;
import com.reactivelab.r2dbc.service.ProductService;
import io.r2dbc.postgresql.codec.Json;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ContextConfiguration(initializers = PersistenceIntegrationTest.Initializer.class)
class PersistenceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("reactivelab")
            .withUsername("user")
            .withPassword("pass");

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.r2dbc.url=" + postgres.getJdbcUrl().replace("jdbc:", "r2dbc:"),
                    "spring.r2dbc.username=" + postgres.getUsername(),
                    "spring.r2dbc.password=" + postgres.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Test
    void scenario1_verifyConnectivity() {
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    void scenario2_crudWithJsonb() {
        Product p = Product.builder()
                .name("Test Product")
                .price(49.99)
                .stock(10)
                .metadata(Json.of("{\"category\": \"electronics\"}"))
                .build();

        webTestClient.post()
                .uri("/api/persistence/products")
                .bodyValue(p)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.metadata.category").isEqualTo("electronics");
    }

    @Test
    void scenario3_templateComparison() {
        // Setup data
        productRepository.deleteAll().block();
        productRepository.save(Product.builder().name("Gaming Laptop").price(1200.0).stock(5).build()).block();
        productRepository.save(Product.builder().name("Office Mouse").price(25.0).stock(50).build()).block();

        // 1. DatabaseClient search (by name)
        StepVerifier.create(productService.searchByName("Gaming"))
                .assertNext(product -> assertThat(product.getPrice()).isEqualTo(1200.0))
                .verifyComplete();

        // 2. R2dbcEntityTemplate search (by price range)
        StepVerifier.create(productService.searchByPriceRange(10.0, 50.0))
                .assertNext(product -> assertThat(product.getName()).isEqualTo("Office Mouse"))
                .verifyComplete();
    }

    @Test
    void scenario4_transactionalRollback() {
        orderRepository.deleteAll().block();
        Product p = productRepository.save(Product.builder().name("Stocked Product").price(50.0).stock(5).build()).block();
        Long productId = p.getId();

        // Purchase 10 (more than 5) -> Should fail with "Insufficient stock"
        webTestClient.post()
                .uri("/api/persistence/purchase?productId=" + productId + "&quantity=10")
                .exchange()
                .expectStatus().is5xxServerError();

        // Verify stock is still 5
        StepVerifier.create(productRepository.findById(productId))
                .assertNext(product -> assertThat(product.getStock()).isEqualTo(5))
                .verifyComplete();
                
        // Verify no order was created
        StepVerifier.create(orderRepository.count())
                .expectNext(0L)
                .verifyComplete();
    }
}
