package com.reactivelab.mongodb;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
class MongoService {

    private final LogRepository logRepository;
    private final ProductRepository productRepository;
    private final ReactiveMongoTemplate mongoTemplate;
    private final ReactiveGridFsTemplate gridFsTemplate;

    // Use replay(1) to ensure the latest event is caught by new subscribers (like late WebTestClient calls)
    private final Sinks.Many<Product> productSink = Sinks.many().replay().limit(1);

    // Scenario 1: Tailable Cursor
    public Flux<LogEntry> streamLogs() {
        return logRepository.findAllBy()
                .doOnNext(entry -> log.info("Tailable Cursor -> New Log: {}", entry.getMessage()));
    }

    public Mono<LogEntry> insertLog(String message, String level) {
        return logRepository.save(LogEntry.builder()
                .message(message)
                .level(level)
                .timestamp(Instant.now())
                .build());
    }

    // Scenario 2: Change Streams
    public Flux<Product> watchProducts() {
        return productSink.asFlux();
    }

    public void notifyProductChange(Product product) {
        log.info("Emitting product to sink: {}", product.getName());
        productSink.tryEmitNext(product);
    }

    // Scenario 4: Aggregations
    public Flux<Map> getCategoryAnalytics() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("category")
                        .sum("amount").as("totalSales")
                        .count().as("transactionCount"),
                Aggregation.sort(org.springframework.data.domain.Sort.Direction.DESC, "totalSales")
        );

        return mongoTemplate.aggregate(aggregation, "sales", Map.class);
    }

    public Mono<String> uploadFile(Mono<FilePart> filePartMono) {
        return filePartMono.flatMap(fp -> 
            gridFsTemplate.store(fp.content(), fp.filename())
                    .map(objectId -> objectId.toHexString())
        );
    }
}

@Configuration
@Slf4j
class MongoConfiguration {

    @Bean
    public ApplicationRunner initCollections(ReactiveMongoTemplate mongoTemplate) {
        return args -> {
            String collectionName = "system_logs";
            mongoTemplate.collectionExists(collectionName)
                    .flatMap(exists -> {
                        if (!exists) {
                            log.info("Creating capped collection: {}", collectionName);
                            return mongoTemplate.createCollection(collectionName,
                                    CollectionOptions.empty().capped().size(1024 * 1024).maxDocuments(1000))
                                    .then(mongoTemplate.save(LogEntry.builder()
                                            .message("Collection initialized")
                                            .level("SYSTEM")
                                            .timestamp(Instant.now())
                                            .build()));
                        }
                        return Mono.empty();
                    })
                    .subscribe(
                            c -> log.info("Collection {} is ready and seeded", collectionName),
                            e -> log.error("Error initializing collection", e)
                    );
        };
    }

    @Bean
    public ApplicationRunner startChangeStreamWatcher(ReactiveMongoTemplate mongoTemplate, MongoService mongoService) {
        return args -> {
            log.info("Starting Change Stream Watcher for 'products'...");
            
            // Periodically check if collection exists and then start watching
            mongoTemplate.collectionExists("products")
                    .flatMap(exists -> exists ? Mono.empty() : mongoTemplate.createCollection("products"))
                    .thenMany(mongoTemplate.changeStream(Product.class)
                            .watchCollection("products")
                            .listen())
                    .doOnNext(event -> {
                        log.info("Change Stream Detected: {} on {}", event.getOperationType(), event.getCollectionName());
                        if (event.getBody() != null) {
                            mongoService.notifyProductChange(event.getBody());
                        }
                    })
                    .doOnError(e -> log.error("CRITICAL: Change Stream Watcher failed", e))
                    .retryWhen(reactor.util.retry.Retry.fixedDelay(5, Duration.ofSeconds(2)))
                    .subscribe();
        };
    }
}

@RestController
@RequestMapping("/api/mongo")
@RequiredArgsConstructor
class MongoController {

    private final MongoService mongoService;

    @GetMapping(value = "/logs/stream", produces = "text/event-stream")
    public Flux<LogEntry> streamLogs() {
        return mongoService.streamLogs();
    }

    @PostMapping("/logs")
    public Mono<LogEntry> createLog(@RequestBody Map<String, String> body) {
        return mongoService.insertLog(body.get("message"), body.get("level"));
    }

    @GetMapping(value = "/products/watch", produces = "text/event-stream")
    public Flux<Product> watchProducts() {
        return mongoService.watchProducts();
    }

    @GetMapping("/analytics/categories")
    public Flux<Map> getAnalytics() {
        return mongoService.getCategoryAnalytics();
    }
}
