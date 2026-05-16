package com.reactivelab.mongodb.config;

import com.reactivelab.mongodb.model.LogEntry;
import com.reactivelab.mongodb.model.Product;
import com.reactivelab.mongodb.service.MongoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@Configuration
@Slf4j
public class MongoConfiguration {

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
