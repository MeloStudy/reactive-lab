package com.reactivelab.foundations;

import reactor.core.publisher.Mono;

import java.util.function.Supplier;

/**
 * Demonstrates the Lazy Execution principle of Project Reactor.
 * Nothing happens until you subscribe.
 */
public class LazyGreeter {

    private final Supplier<String> messageSupplier;

    public LazyGreeter(Supplier<String> messageSupplier) {
        this.messageSupplier = messageSupplier;
    }

    /**
     * Creates a Mono from a Callable/Supplier.
     * The supplier is ONLY called when someone subscribes to the Mono.
     */
    public Mono<String> getGreeting() {
        return Mono.fromCallable(messageSupplier::get);
    }
}
