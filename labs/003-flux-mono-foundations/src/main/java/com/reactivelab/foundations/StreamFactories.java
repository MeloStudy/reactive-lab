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
}
