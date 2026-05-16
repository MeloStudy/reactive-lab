package com.reactivelab.mongodb.controller;

import com.reactivelab.mongodb.model.LogEntry;
import com.reactivelab.mongodb.model.Product;
import com.reactivelab.mongodb.service.MongoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.bson.Document;

import java.util.Map;

@RestController
@RequestMapping("/api/mongo")
@RequiredArgsConstructor
public class MongoController {

    private final MongoService mongoService;

    @GetMapping(value = "/logs/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<LogEntry> streamLogs() {
        return mongoService.streamLogs();
    }

    @GetMapping(value = "/logs/stream-backpressure", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<LogEntry> streamLogsWithBackpressure() {
        return mongoService.streamLogsWithBackpressure();
    }

    @PostMapping("/logs")
    public Mono<LogEntry> createLog(@RequestBody Map<String, String> body) {
        return mongoService.insertLog(body.get("message"), body.get("level"));
    }

    @GetMapping(value = "/products/watch", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Product> watchProducts() {
        return mongoService.watchProducts();
    }

    @GetMapping("/analytics/categories")
    public Flux<Document> getAnalytics() {
        return mongoService.getCategoryAnalytics();
    }

    @PostMapping(value = "/files/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> uploadFile(@RequestPart("file") Mono<FilePart> filePartMono) {
        return mongoService.uploadFile(filePartMono);
    }

    @GetMapping("/files/download/{filename}")
    public ResponseEntity<Flux<DataBuffer>> downloadFile(@PathVariable String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(mongoService.downloadFile(filename));
    }
}
