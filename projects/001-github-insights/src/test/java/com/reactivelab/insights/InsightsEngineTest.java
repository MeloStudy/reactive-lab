package com.reactivelab.insights;

import com.reactivelab.insights.model.GitHubEvent;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class InsightsEngineTest {

    @Test
    void shouldProcessFileAndHandleErrors() {
        InsightsEngine engine = new InsightsEngine();
        String path = "src/test/resources/events.json";

        Flux<GitHubEvent> pipeline = engine.runPipeline(path);

        StepVerifier.create(pipeline)
                .expectNextCount(8) // 9 lines, 1 invalid -> 8 events
                .verifyComplete();

        assertThat(engine.getErrorCount()).isEqualTo(1);
    }

    @Test
    void shouldCalculateTopMetrics() {
        InsightsEngine engine = new InsightsEngine();
        AnalyticsService analytics = new AnalyticsService();
        String path = "src/test/resources/events.json";

        Flux<GitHubEvent> pipeline = engine.runPipeline(path).cache();

        // Verify Top Repositories (Stars)
        StepVerifier.create(analytics.getTopRepositories(pipeline, 5))
                .assertNext(map -> {
                    assertThat(map).containsEntry("repo/a", 3L);
                    assertThat(map).containsEntry("repo/b", 1L);
                })
                .verifyComplete();

        // Verify Top Languages
        StepVerifier.create(analytics.getTopLanguages(pipeline, 5))
                .assertNext(map -> {
                    assertThat(map).containsEntry("Java", 2L);
                    assertThat(map).containsEntry("Python", 1L);
                })
                .verifyComplete();
    }
}
