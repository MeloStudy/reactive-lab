package com.reactivelab.observability;

import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.observability.micrometer.Micrometer;

@Service
@RequiredArgsConstructor
@Slf4j
class ObservabilityService {

    private final WebClient webClient;
    private final ObservationRegistry observationRegistry;

    // Scenario 1: WebClient tracing
    public Mono<String> callDownstream(String url) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> log.info("Downstream response: {}", response));
    }

    // Scenario 2: Custom Observation
    public Mono<String> processCustom(String userId) {
        return Mono.just("Processed: " + userId)
                .name("custom.process") // Used by Micrometer to name the metric/span
                .tag("process.type", "user-process") // Low cardinality
                .tap(Micrometer.observation(observationRegistry));
                // Note: .tag() on Mono creates a low cardinality tag. High cardinality tags
                // require manually creating an Observation, but we keep it simple here.
    }
    

}
