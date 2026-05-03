package com.reactivelab.operators;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;

/**
 * Scenario 4: Demonstrates "latest-only" flattening with switchMap.
 */
public class SearchDebouncer {

    /**
     * Simulates a debounced search.
     * Rule: switchMap cancels the previous inner subscription when a new item arrives from the source.
     */
    public Flux<String> debounceSearch(Flux<String> queries) {
        return queries.switchMap(query -> 
            Mono.delay(Duration.ofMillis(100))
                .map(l -> "Result for: " + query));
    }
}
