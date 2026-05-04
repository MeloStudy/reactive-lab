package com.reactivelab.lifecycle;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;

/**
 * Scenario 3: The Greedy Subscriber
 * Demonstrates manual demand control by extending BaseSubscriber.
 */
@Slf4j
public class SmartSubscriber<T> extends BaseSubscriber<T> {

    private int receivedCount = 0;

    @Override
    protected void hookOnSubscribe(Subscription subscription) {
        log.info("Subscribed! Initial request: 1");
        // Manual demand control: start by requesting only 1 item
        request(1);
    }

    @Override
    protected void hookOnNext(T value) {
        log.info("Processing: {}", value);
        receivedCount++;

        // Manual demand control: request the next item only after processing the current one
        request(1);
    }

    public int getReceivedCount() {
        return receivedCount;
    }
}
