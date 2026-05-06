package com.reactivelab.orchestration;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Scenario 3: Demonstrates the Recovery Ladder (Return -> Map -> Resume).
 * 
 * Reactive error handling is about catching the error signal before it
 * reaches the subscriber and terminates the subscription.
 */
@Slf4j
public class ResilientClient {

    /**
     * Tries a call and returns a default value if it fails.
     * 
     * [PEDAGOGICAL NOTE]:
     * onErrorReturn is a STATIC fallback. It swallows the error and
     * emits a single value, then completes the stream.
     */
    public Mono<String> callWithFallback(Mono<String> remoteCall, String defaultValue) {
        return remoteCall
                .doOnError(e -> log.error("Remote call failed, applying fallback: {}", e.getMessage()))
                .onErrorReturn(defaultValue);
    }

    /**
     * Tries a call and translates any technical exception into a BusinessException.
     * 
     * [PEDAGOGICAL NOTE]:
     * onErrorMap is used for EXCEPTION TRANSLATION. It allows you to
     * wrap low-level errors (like SQL or Network exceptions) into
     * high-level business domain exceptions.
     */
    public Mono<String> callWithTranslation(Mono<String> remoteCall) {
        return remoteCall
                .onErrorMap(e -> {
                    log.warn("Mapping technical error to business domain: {}", e.getClass().getSimpleName());
                    return new BusinessException("Service Unavailable: " + e.getMessage(), e);
                });
    }

    /**
     * Tries Source A. If it fails, fails over to Source B.
     * 
     * [PEDAGOGICAL NOTE]:
     * onErrorResume is a DYNAMIC fallback. It allows you to switch to
     * an entirely different Publisher (e.g., a secondary API or a cache)
     * when the primary one fails.
     */
    public Mono<String> callWithFailover(Mono<String> sourceA, Mono<String> sourceB) {
        return sourceA.onErrorResume(e -> {
            log.info("Source A failed, switching to Source B (Failover)");
            return sourceB;
        });
    }

    public static class BusinessException extends RuntimeException {
        public BusinessException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
