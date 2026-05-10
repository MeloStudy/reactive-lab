package com.reactivelab.insights;

import com.reactivelab.insights.model.GitHubEvent;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            log.error("Usage: java -jar insights.jar <path-to-json-file>");
            System.exit(1);
        }

        String filePath = args[0];
        String securityDbPath = args.length > 1 ? args[1] : "src/test/resources/security-database.json";
        
        InsightsEngine engine = new InsightsEngine();
        AnalyticsService analytics = new AnalyticsService();
        SecurityService securityService = new SecurityService(securityDbPath);

        log.info("🚀 Starting Reactive GitHub Insights Engine...");
        Instant start = Instant.now();

        // Run the pipeline and share it among 3 subscribers (count, repos, langs)
        Flux<GitHubEvent> eventStream = engine.runPipeline(filePath).publish().autoConnect(3);

        // Calculate metrics
        Mono<Long> totalCount = eventStream.count();
        Mono<Map<String, Long>> topReposMap = analytics.getTopRepositories(eventStream, 5);
        Mono<Map<String, Long>> topLangs = analytics.getTopLanguages(eventStream, 5);
        
        // Enrich Top Repos with Security Data
        Mono<List<AnalyticsService.RepoReport>> enrichedRepos = 
            analytics.enrichTopRepositories(topReposMap, securityService);

        // Zip all metrics together
        Mono.zip(totalCount, enrichedRepos, topLangs)
            .doOnNext(tuple -> {
                Duration duration = Duration.between(start, Instant.now());
                printSummary(tuple.getT1(), engine.getErrorCount(), duration, tuple.getT2(), tuple.getT3());
            })
            .block();

        // Ensure clean exit to avoid Maven thread warnings
        System.exit(0);
    }

    private static void printSummary(long total, long errors, Duration duration, 
                                     List<AnalyticsService.RepoReport> repos, Map<String, Long> langs) {
        String nl = System.lineSeparator();
        StringBuilder sb = new StringBuilder();
        sb.append(nl).append("========================================").append(nl);
        sb.append("📊 EXECUTIVE SUMMARY").append(nl);
        sb.append("========================================").append(nl);
        sb.append(String.format("Total Events Processed: %d%n", total));
        sb.append(String.format("Malformed Lines Skipped: %d%n", errors));
        sb.append(String.format("Total Time: %d ms%n", duration.toMillis()));
        sb.append("----------------------------------------").append(nl);
        sb.append("🏆 TOP 5 REPOSITORIES & SECURITY AUDIT").append(nl);
        repos.forEach(report -> 
            sb.append(String.format("- %s: %d stars | Vulnerabilities: %d%n", 
                report.name(), report.stars(), report.vulnerabilities())));
        sb.append("----------------------------------------").append(nl);
        sb.append("🌍 TOP 5 LANGUAGES (PR Activity)").append(nl);
        langs.forEach((name, count) -> sb.append(String.format("- %s: %d PRs%n", name, count)));
        sb.append("========================================").append(nl);
        
        log.info("{}", sb.toString());
    }
}
