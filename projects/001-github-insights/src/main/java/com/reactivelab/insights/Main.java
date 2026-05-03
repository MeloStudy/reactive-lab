package com.reactivelab.insights;

import com.reactivelab.insights.model.GitHubEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java -jar insights.jar <path-to-json-file>");
            System.exit(1);
        }

        String filePath = args[0];
        InsightsEngine engine = new InsightsEngine();
        AnalyticsService analytics = new AnalyticsService();

        System.out.println("🚀 Starting Reactive GitHub Insights Engine...");
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
        System.out.println("\n========================================");
        System.out.println("📊 EXECUTIVE SUMMARY");
        System.out.println("========================================");
        System.out.printf("Total Events Processed: %d\n", total);
        System.out.printf("Malformed Lines Skipped: %d\n", errors);
        System.out.printf("Total Time: %d ms\n", duration.toMillis());
        System.out.println("----------------------------------------");
        System.out.println("🏆 TOP 5 REPOSITORIES (Stars)");
        repos.forEach((name, count) -> System.out.printf("- %s: %d stars\n", name, count));
        System.out.println("----------------------------------------");
        System.out.println("🌍 TOP 5 LANGUAGES (PR Activity)");
        langs.forEach((name, count) -> System.out.printf("- %s: %d PRs\n", name, count));
        System.out.println("========================================\n");
    }
}
