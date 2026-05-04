package com.reactivelab.orchestration;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Scenario 3: Demonstrates the Recovery Ladder (Return -> Map -> Resume).
 */
@Slf4j
public class ResilientClient {

    /**
     * Tries a call and returns a default value if it fails.
     * Note: This swallows the error, so the translation in the pipeline is not visible to the subscriber.
     */
    public Mono<String> callWithFallback(Mono<String> remoteCall, String defaultValue) {
        return remoteCall
                .doOnError(e -> log.error("Remote call failed: {}", e.getMessage()))
                .onErrorMap(e -> new BusinessException("Service Unavailable", e))
                .onErrorReturn(defaultValue);
    }

    /**
     * Tries a call and translates any technical exception into a BusinessException.
     */
    public Mono<String> callWithTranslation(Mono<String> remoteCall) {
        return remoteCall
                .onErrorMap(e -> new BusinessException("Service Translation: " + e.getMessage(), e));
    }

    /**
     * Tries Source A. If it fails, fails over to Source B.
     * Rule: onErrorResume swallows the error and switches to a new Publisher.
     */
    public Mono<String> callWithFailover(Mono<String> sourceA, Mono<String> sourceB) {
        return sourceA.onErrorResume(e -> sourceB);
    }

    public static class BusinessException extends RuntimeException {
        public BusinessException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
