package com.reactivelab.insights;

import com.reactivelab.insights.model.GitHubEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnalyticsService {

    public Mono<Map<String, Long>> getTopRepositories(Flux<GitHubEvent> events, int limit) {
        return events
                .filter(e -> "WatchEvent".equals(e.getType()))
                .groupBy(e -> e.getRepo().getName())
                .flatMap(group -> group.count().map(count -> Map.entry(group.key(), count)))
                .collectList()
                .map(list -> list.stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                        .limit(limit)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    public Mono<Map<String, Long>> getTopLanguages(Flux<GitHubEvent> events, int limit) {
        return events
                .filter(e -> "PullRequestEvent".equals(e.getType()))
                .map(e -> e.getPayload().getPullRequest().getBase().getRepo().getLanguage())
                .filter(lang -> lang != null && !lang.isEmpty())
                .groupBy(lang -> lang)
                .flatMap(group -> group.count().map(count -> Map.entry(group.key(), count)))
                .collectList()
                .map(list -> list.stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                        .limit(limit)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    public record RepoReport(String name, long stars, int vulnerabilities) {}

    public Mono<List<RepoReport>> enrichTopRepositories(Mono<Map<String, Long>> topRepos, SecurityService securityService) {
        return topRepos
                .flatMapMany(map -> Flux.fromIterable(map.entrySet()))
                .flatMap(entry -> securityService.getVulnerabilityCount(entry.getKey())
                        .map(v -> new RepoReport(entry.getKey(), entry.getValue(), v)))
                .collectList()
                .map(list -> list.stream()
                        .sorted((a, b) -> Long.compare(b.stars(), a.stars()))
                        .toList());
    }
}
