package com.reactivelab.foundations;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.concurrent.Callable;

/**
 * Introduction to common factory methods for creating Flux and Mono.
 */
public class StreamFactories {

    public Flux<String> fromCollection(Collection<String> items) {
        return Flux.fromIterable(items);
    }

    public Mono<Integer> fromDangerousOperation(Callable<Integer> operation) {
        return Mono.fromCallable(operation);
    }

    public Mono<Object> alwaysEmpty() {
        return Mono.empty();
    }

    public Flux<Object> alwaysError() {
        return Flux.error(new IllegalStateException("Boom!"));
    }

    /**
     * Demonstrates bridging a standard Java 8+ Stream into a Flux.
     * Note: Standard Java Streams are 1-time-use, unlike Flux.
     */
    public <T> Flux<T> fromJavaStream(java.util.stream.Stream<T> stream) {
        return Flux.fromStream(stream);
    }
}
