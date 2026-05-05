package com.reactivelab.mongodb;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ContextConfiguration(initializers = PersistenceIntegrationTest.Initializer.class)
class PersistenceIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @org.junit.jupiter.api.BeforeEach
    void setup() {
        this.webTestClient = webTestClient.mutate()
                .responseTimeout(Duration.ofSeconds(30))
                .build();
    }

    @Container
    static GenericContainer<?> mongo = new GenericContainer<>(DockerImageName.parse("mongo:6.0"))
            .withExposedPorts(27017)
            .withCommand("--replSet", "rs0", "--bind_ip_all")
            .waitingFor(Wait.forLogMessage(".*Waiting for connections.*\\n", 1));

    static {
        mongo.start();
        try {
            // Initiate replica set
            mongo.execInContainer("mongosh", "--eval", "rs.initiate()");
            Thread.sleep(5000); // Wait for election
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            // CRITICAL: Use directConnection=true to avoid the driver trying to connect to internal container hostnames
            String url = String.format("mongodb://%s:%d/test?replicaSet=rs0&directConnection=true", mongo.getHost(), mongo.getMappedPort(27017));
            TestPropertyValues.of(
                    "spring.data.mongodb.uri=" + url
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SaleRepository saleRepository;

    @Test
    void scenario1_tailableCursorInfiniteStream() {
        try { Thread.sleep(5000); } catch (InterruptedException e) {}

        Flux<LogEntry> logStream = webTestClient.get()
                .uri("/api/mongo/logs/stream")
                .exchange()
                .expectStatus().isOk()
                .returnResult(LogEntry.class)
                .getResponseBody()
                .filter(entry -> !"SYSTEM".equals(entry.getLevel()));

        StepVerifier.create(logStream)
                .thenAwait(Duration.ofSeconds(2))
                .then(() -> {
                    webTestClient.post().uri("/api/mongo/logs").bodyValue(Map.of("message", "Log 1", "level", "INFO")).exchange();
                    webTestClient.post().uri("/api/mongo/logs").bodyValue(Map.of("message", "Log 2", "level", "INFO")).exchange();
                })
                .assertNext(log -> assertThat(log.getMessage()).isEqualTo("Log 1"))
                .assertNext(log -> assertThat(log.getMessage()).isEqualTo("Log 2"))
                .thenCancel()
                .verify(Duration.ofSeconds(30));
    }

    @Test
    void scenario2_changeStreamNotifications() {
        // First, ensure the collection exists by saving one product
        productRepository.save(Product.builder().name("Initial").price(0.0).build()).block(Duration.ofSeconds(10));

        Flux<Product> productWatcher = webTestClient.get()
                .uri("/api/mongo/products/watch")
                .exchange()
                .expectStatus().isOk()
                .returnResult(Product.class)
                .getResponseBody()
                .filter(p -> !"Initial".equals(p.getName()));

        StepVerifier.create(productWatcher)
                .thenAwait(Duration.ofSeconds(5))
                .then(() -> {
                    productRepository.save(Product.builder().name("New Product").price(99.99).build()).block(Duration.ofSeconds(10));
                })
                .assertNext(product -> {
                    assertThat(product.getName()).isEqualTo("New Product");
                })
                .thenCancel()
                .verify(Duration.ofSeconds(30));
    }

    @Test
    void scenario4_aggregations() {
        saleRepository.save(Sale.builder().category("Electronics").amount(500.0).quantity(1).build()).block(Duration.ofSeconds(10));
        saleRepository.save(Sale.builder().category("Electronics").amount(300.0).quantity(1).build()).block(Duration.ofSeconds(10));

        webTestClient.get()
                .uri("/api/mongo/analytics/categories")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Map.class)
                .consumeWith(result -> {
                    Map first = (Map) result.getResponseBody().get(0);
                    assertThat(first.get("_id")).isEqualTo("Electronics");
                    assertThat(first.get("totalSales")).isEqualTo(800.0);
                });
    }
}
