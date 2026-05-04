package com.reactivelab.insights;

import com.reactivelab.insights.model.GitHubEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
}
