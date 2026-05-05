package com.reactivelab.r2dbc;

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
    void scenario3_customSqlDatabaseClient() {
        productRepository.save(Product.builder().name("Cheap").price(10.0).stock(5).build()).block();
        productRepository.save(Product.builder().name("Expensive").price(200.0).stock(5).build()).block();

        StepVerifier.create(productService.findExpensiveProducts(100.0))
                .assertNext(product -> assertThat(product.getName()).isEqualTo("Expensive"))
                .verifyComplete();
    }

    @Test
    void scenario4_transactionalRollback() {
        Product p = productRepository.save(Product.builder().name("Stocked Product").price(50.0).stock(5).build()).block();
        Long productId = p.getId();

        // This will fail in order creation if we simulate a failure
        // We'll trigger an error by trying to purchase more than available, or we can mock a failure.
        // Actually, let's trigger a RuntimeException in the service for a specific condition.
        
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
                .expectNext(0L) // Assuming clean DB for this test
                .verifyComplete();
    }
}
