package com.reactivelab.streams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

/**
 * A testing implementation of Subscriber that captures received items
 * and provides access to the Subscription for manual demand control.
 */
public class CustomSubscriber<T> implements Subscriber<T> {

    private final List<T> items = new ArrayList<>();
    private Subscription subscription;
    private Throwable error;
    private boolean completed = false;

    @Override
    public void onSubscribe(Subscription s) {
        // Rule 2.5: A Subscriber MUST NOT be called with more than one Subscription
        if (this.subscription != null) {
            s.cancel();
            return;
        }
        this.subscription = s;
    }

    @Override
    public void onNext(T t) {
        // Rule 2.13: onNext signals MUST be processed sequentially
        items.add(t);
    }

    @Override
    public void onError(Throwable t) {
        // Rule 2.4: Subscriber.onError MUST be terminal
        this.error = t;
    }

    @Override
    public void onComplete() {
        // Rule 2.4: Subscriber.onComplete MUST be terminal
        this.completed = true;
    }

    // Helper methods for testing
    
    public void request(long n) {
        if (subscription != null) {
            subscription.request(n);
        }
    }

    public void cancel() {
        if (subscription != null) {
            subscription.cancel();
        }
    }

    public List<T> getItems() {
        return items;
    }

    public Throwable getError() {
        return error;
    }

    public boolean isCompleted() {
        return completed;
    }
}
