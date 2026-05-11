package com.reactivelab.orchestration;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

/**
 * Scenario 11: Resource Safety & Discard Support.
 * Demonstrates how to handle elements that are "dropped" by operators.
 */
@Slf4j
public class ResourceSafetyService {

    /**
     * Filters a stream and ensures that any discarded items are properly handled.
     * <p>
     * [PEDAGOGICAL NOTE]:
     * doOnDiscard is a hook that triggers when an element is dropped by an upstream
     * or internal operator (e.g., filter, take, buffer).
     * This is critical for cleaning up resources like pooled buffers or open handles.
     *
     * @param source      The source flux
     * @param predicate   The filter condition
     * @param discardHook A consumer to handle discarded items
     * @return A filtered Flux
     */
    public <T> Flux<T> filterWithCleanup(Flux<T> source, java.util.function.Predicate<T> predicate, Consumer<T> discardHook) {
        return source
                .filter(predicate)
                .doOnDiscard(Object.class, (Object item) -> {
                    log.warn("Discarding item: {}", item);
                    // Safe cast since we know the source type
                    @SuppressWarnings("unchecked")
                    T typedItem = (T) item;
                    discardHook.accept(typedItem);
                });
    }
}
