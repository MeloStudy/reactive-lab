package com.reactivelab.lifecycle;

import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.publisher.Flux;
import java.time.Duration;

/**
 * Scenario 2: The Resource Manager
 * Demonstrates managing multiple subscriptions using a Composite Disposable.
 */
public class SubscriptionGroup {

    private final Disposable.Composite composite = Disposables.composite();

    /**
     * Subscribes to multiple streams and adds them to the composite.
     */
    @SafeVarargs
    public final void addSubscriptions(Flux<Long>... fluxes) {
        for (Flux<Long> flux : fluxes) {
            Disposable d = flux.subscribe(
                item -> System.out.println("Group Received: " + item)
            );
            composite.add(d);
        }
    }

    /**
     * Disposes all subscriptions in the group.
     */
    public void stopAll() {
        composite.dispose();
    }

    /**
     * Checks if all subscriptions are disposed.
     */
    public boolean areAllStopped() {
        return composite.isDisposed();
    }
}
