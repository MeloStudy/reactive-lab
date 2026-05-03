package com.reactivelab.insights;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reactivelab.insights.model.GitHubEvent;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class InsightsEngine {

    private final ObjectMapper mapper = new ObjectMapper();
    private final AtomicLong errorCount = new AtomicLong(0);

    public record InsightResult(
        long totalProcessed,
        long errors,
        Map<String, Long> topRepositories,
        Map<String, Long> topLanguages
    ) {}

    public Flux<GitHubEvent> runPipeline(String filePath) {
        Path path = Paths.get(filePath);

        return Flux.using(
            () -> Files.lines(path),
            Flux::fromStream,
            Stream::close
        )
        // Offload JSON parsing to parallel threads
        .publishOn(Schedulers.parallel())
        .flatMap(line -> {
            try {
                return Flux.just(mapper.readValue(line, GitHubEvent.class));
            } catch (Exception e) {
                errorCount.incrementAndGet();
                return Flux.empty();
            }
        })
        // Filter events of interest
        .filter(event -> 
            "PushEvent".equals(event.getType()) || 
            "WatchEvent".equals(event.getType()) || 
            "PullRequestEvent".equals(event.getType())
        );
    }

    public long getErrorCount() {
        return errorCount.get();
    }
}
