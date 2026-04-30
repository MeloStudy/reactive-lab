package com.reactivelab.spec;

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
        this.subscription = s;
    }

    @Override
    public void onNext(T t) {
        items.add(t);
    }

    @Override
    public void onError(Throwable t) {
        this.error = t;
    }

    @Override
    public void onComplete() {
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
