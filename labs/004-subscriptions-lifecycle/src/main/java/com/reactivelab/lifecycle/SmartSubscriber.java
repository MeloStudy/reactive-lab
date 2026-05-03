package com.reactivelab.lifecycle;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;

/**
 * Scenario 3: The Greedy Subscriber
 * Demonstrates manual demand control by extending BaseSubscriber.
 */
public class SmartSubscriber<T> extends BaseSubscriber<T> {

    private int receivedCount = 0;

    @Override
    protected void hookOnSubscribe(Subscription subscription) {
        System.out.println("Subscribed! Initial request: 1");
        // Manual demand control: start by requesting only 1 item
        request(1);
    }

    @Override
    protected void hookOnNext(T value) {
        System.out.println("Processing: " + value);
        receivedCount++;
        
        // Manual demand control: request the next item only after processing the current one
        request(1);
    }

    public int getReceivedCount() {
        return receivedCount;
    }
}
