package com.reactivelab.mongodb.repository;

import com.reactivelab.mongodb.model.LogEntry;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

public interface LogRepository extends ReactiveMongoRepository<LogEntry, String> {
    @Tailable
    Flux<LogEntry> findByLevel(String level);
    
    @Tailable
    Flux<LogEntry> findAllBy();
}
