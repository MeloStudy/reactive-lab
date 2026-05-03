package com.reactivelab.orchestration;

import reactor.core.publisher.Mono;
import java.util.function.Function;

/**
 * Scenario 3: Demonstrates the Recovery Ladder (Return -> Map -> Resume).
 */
public class ResilientClient {

    /**
     * Tries a call. 
     * 1. Logs error (Side-effect).
     * 2. Maps technical exception to business exception (Translation).
     * 3. Returns a default value if it fails (Fallback).
     */
    public Mono<String> callWithFallback(Mono<String> remoteCall, String defaultValue) {
        return remoteCall
            .doOnError(e -> System.err.println("Remote call failed: " + e.getMessage())) // Side-effect
            .onErrorMap(e -> new BusinessException("Service Unavailable", e)) // Translation
            .onErrorReturn(defaultValue); // Static Fallback
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
