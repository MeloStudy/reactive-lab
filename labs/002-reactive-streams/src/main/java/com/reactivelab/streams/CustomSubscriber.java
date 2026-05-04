package com.reactivelab.streams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

/**
 * A testing implementation of Subscriber that captures received items
 * and provides access to the Subscription for manual demand control.
 * <p>
 * PEDAGOGICAL NOTE:
 * In a real application, the Subscriber is where the "Business Logic" lives.
 * Here, we use it to demonstrate how a Subscriber interacts with a Subscription.
 */
public class CustomSubscriber<T> implements Subscriber<T> {

    // Internal state to track what we've received
    private final List<T> items = new ArrayList<>();
    private Subscription subscription;
    private Throwable error;
    private boolean completed = false;

    @Override
    public void onSubscribe(Subscription s) {
        // Rule 2.5: A Subscriber MUST NOT be called with more than one Subscription.
        // If we already have one, we MUST cancel the new one to prevent resource leaks.
        if (this.subscription != null) {
            s.cancel();
            return;
        }
        this.subscription = s;
        
        // Note: We don't call request() here automatically. 
        // This allows the test to control demand manually.
    }

    @Override
    public void onNext(T t) {
        // Rule 2.13: onNext signals MUST be processed sequentially.
        // In a real subscriber, you would perform your processing here.
        if (t == null) throw new NullPointerException("Rule 2.13: onNext signal cannot be null");
        items.add(t);
    }

    @Override
    public void onError(Throwable t) {
        // Rule 2.4: Subscriber.onError MUST be terminal. 
        // Once this is called, no more signals (onNext, onComplete) should be received.
        if (t == null) throw new NullPointerException("Rule 2.13: onError signal cannot be null");
        this.error = t;
    }

    @Override
    public void onComplete() {
        // Rule 2.4: Subscriber.onComplete MUST be terminal.
        this.completed = true;
    }

    // --- HELPER METHODS FOR TESTING ---
    // These allow us to "simulate" a subscriber's behavior from our tests.

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
