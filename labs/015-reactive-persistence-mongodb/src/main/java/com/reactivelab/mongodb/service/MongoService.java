package com.reactivelab.mongodb.service;

import com.reactivelab.mongodb.model.LogEntry;
import com.reactivelab.mongodb.model.Product;
import com.reactivelab.mongodb.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.bson.Document;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MongoService {

    private final LogRepository logRepository;
    private final ReactiveMongoTemplate mongoTemplate;
    private final ReactiveGridFsTemplate gridFsTemplate;

    // Use replay(1) to ensure the latest event is caught by new subscribers
    private final Sinks.Many<Product> productSink = Sinks.many().replay().limit(1);

    // Scenario 1: Tailable Cursor
    public Flux<LogEntry> streamLogs() {
        return logRepository.findAllBy()
                .doOnNext(entry -> log.info("Tailable Cursor -> New Log: {}", entry.getMessage()));
    }

    // Scenario 5: Backpressure Handling
    public Flux<LogEntry> streamLogsWithBackpressure() {
        return logRepository.findAllBy()
                .onBackpressureDrop(dropped -> log.warn("BACKPRESSURE DROP: Slow consumer missed log -> {}", dropped.getMessage()))
                .doOnNext(entry -> log.info("Tailable Cursor (BP) -> Processing Log: {}", entry.getMessage()));
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

    // Scenario 3: GridFS File Storage
    public Mono<String> uploadFile(Mono<FilePart> filePartMono) {
        return filePartMono.flatMap(fp -> 
            gridFsTemplate.store(fp.content(), fp.filename())
                    .map(objectId -> objectId.toHexString())
        );
    }

    public Flux<DataBuffer> downloadFile(String filename) {
        return gridFsTemplate.getResource(filename)
                .flatMapMany(resource -> resource.getDownloadStream());
    }

    // Scenario 4: Aggregations
    public Flux<Document> getCategoryAnalytics() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("category")
                        .sum("amount").as("totalSales")
                        .count().as("transactionCount"),
                Aggregation.sort(org.springframework.data.domain.Sort.Direction.DESC, "totalSales")
        );

        return mongoTemplate.aggregate(aggregation, "sales", Document.class);
    }
}
