package com.reactivelab.insights;

import com.reactivelab.insights.model.GitHubEvent;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Slf4j
public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            log.error("Usage: java -jar insights.jar <path-to-json-file>");
            System.exit(1);
        }

        String filePath = args[0];
        InsightsEngine engine = new InsightsEngine();
        AnalyticsService analytics = new AnalyticsService();

        log.info("🚀 Starting Reactive GitHub Insights Engine...");
        Instant start = Instant.now();

        // Run the pipeline and share it among 3 subscribers (count, repos, langs)
        Flux<GitHubEvent> eventStream = engine.runPipeline(filePath).publish().autoConnect(3);

        // Calculate metrics
        Mono<Long> totalCount = eventStream.count();
        Mono<Map<String, Long>> topRepos = analytics.getTopRepositories(eventStream, 5);
        Mono<Map<String, Long>> topLangs = analytics.getTopLanguages(eventStream, 5);

        // Zip all metrics together
        Mono.zip(totalCount, topRepos, topLangs)
            .doOnNext(tuple -> {
                Duration duration = Duration.between(start, Instant.now());
                printSummary(tuple.getT1(), engine.getErrorCount(), duration, tuple.getT2(), tuple.getT3());
            })
            .block();

        // Ensure clean exit to avoid Maven thread warnings
        System.exit(0);
    }

    private static void printSummary(long total, long errors, Duration duration, 
                                     Map<String, Long> repos, Map<String, Long> langs) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n========================================\n");
        sb.append("📊 EXECUTIVE SUMMARY\n");
        sb.append("========================================\n");
        sb.append(String.format("Total Events Processed: %d\n", total));
        sb.append(String.format("Malformed Lines Skipped: %d\n", errors));
        sb.append(String.format("Total Time: %d ms\n", duration.toMillis()));
        sb.append("----------------------------------------\n");
        sb.append("🏆 TOP 5 REPOSITORIES (Stars)\n");
        repos.forEach((name, count) -> sb.append(String.format("- %s: %d stars\n", name, count)));
        sb.append("----------------------------------------\n");
        sb.append("🌍 TOP 5 LANGUAGES (PR Activity)\n");
        langs.forEach((name, count) -> sb.append(String.format("- %s: %d PRs\n", name, count)));
        sb.append("========================================\n");
        
        log.info("{}", sb.toString());
    }
}
